package de.maltsev.deblock.integration.flights.toughjet

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.equalToJson
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching
import com.github.tomakehurst.wiremock.core.Options.DYNAMIC_PORT
import de.maltsev.deblock.flights.AirlineName
import de.maltsev.deblock.flights.Flight
import de.maltsev.deblock.flights.FlightProvider.TOUGH_JET
import de.maltsev.deblock.fligts.test.TestData.anAirportCode
import de.maltsev.deblock.http.client.HttpClients.createHttpClient
import de.maltsev.deblock.integration.flights.provider.FlightProviderError.InvalidRequest
import de.maltsev.deblock.integration.flights.provider.FlightProviderError.ProviderUnavailable
import de.maltsev.deblock.integration.flights.provider.FlightProviderError.RequestTimeout
import de.maltsev.deblock.integration.flights.provider.FlightsSearchRequest
import de.maltsev.deblock.integration.flights.provider.FlightsSearchResult
import de.maltsev.deblock.integration.flights.provider.PassengerCount
import de.maltsev.deblock.integration.flights.toughjet.ToughJetHttpFlightListProvider.ToughJetClientConfig
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.assertions.asClue
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.wiremock.ListenerMode.PER_SPEC
import io.kotest.extensions.wiremock.WireMockListener
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.alphanumeric
import io.kotest.property.arbitrary.localDate
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.uInt
import io.ktor.client.plugins.HttpRequestTimeoutException
import org.javamoney.moneta.Money
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset.UTC
import kotlin.time.Duration.Companion.seconds

class ToughJetHttpFlightListProviderSpec : BehaviorSpec({
    val server = WireMockServer(DYNAMIC_PORT)
    listener(WireMockListener(server, PER_SPEC))

    val key = Arb.string(50, Codepoint.alphanumeric()).next()

    val client by lazy {
        ToughJetHttpFlightListProvider(
            client = createHttpClient(
                baseUrl = server.baseUrl(),
                connectTimeout = 1.seconds,
                requestTimeout = 2.seconds,
            ),
            config = ToughJetClientConfig(
                url = server.baseUrl(),
                apiKey = key,
            ),
        )
    }

    val airportA = anAirportCode()
    val airportB = anAirportCode(except = airportA)
    val departureAt = Arb.localDate().next()
    val returnAt = departureAt.plusDays(Arb.long(1L..10).next())
    val departureAtInstant = departureAt.atTime(LocalTime.of(10, 25)).toInstant(UTC)
    val returnAtInstant = returnAt.atTime(LocalTime.of(2, 30)).toInstant(UTC)
    val passengerCount = Arb.uInt(1u..10u).next()
    val request = FlightsSearchRequest(
        origin = airportA,
        destination = airportB,
        departureAt = departureAt,
        returnAt = returnAt,
        passengerCount = PassengerCount(adults = passengerCount),
    )
    val requestJson = """
        {
            "from": "$airportA",
            "to": "$airportB",
            "outboundDate": "$departureAt",
            "inboundDate": "$returnAt",
            "numberOfAdults": $passengerCount          
        }
    """.trimIndent()

    Given("Server replies with list of flights") {
        server.stubFor(
            post(urlPathEqualTo("/flights"))
                .withHeader("Authorization", equalTo("Bearer $key"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalToJson(requestJson))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                            """
                                {
                                  "flights": [
                                    {                                   
                                      "carrier": "Japan Airlines",
                                      "basePrice": "EUR 500",
                                      "tax": "EUR 10.00",
                                      "discount": 50,
                                      "departureAirportName": "$airportA",
                                      "arrivalAirportName": "$airportB",
                                      "outboundDateTime": "$departureAtInstant",
                                      "inboundDateTime": "$returnAtInstant"
                                    },
                                    {
                                      "carrier": "Turkish Airlines",
                                      "basePrice": "USD 30.5",
                                      "tax": "USD 2.00",
                                      "discount": 0,
                                      "departureAirportName": "$airportA",
                                      "arrivalAirportName": "$airportB",
                                      "outboundDateTime": "$departureAtInstant",
                                      "inboundDateTime": "$returnAtInstant"
                                    }
                                  ]
                                }
                            """.trimIndent(),
                        ),
                ),
        )

        When("Client requests list of flights") {
            val result = client.search(request)

            Then("Client receives list of flights") {
                result.shouldBeRight().asClue {
                    it.shouldBeInstanceOf<FlightsSearchResult>()
                    it.flights shouldContainExactly listOf(
                        Flight(
                            price = Money.of(260, "EUR"),
                            airline = AirlineName("Japan Airlines"),
                            departure = airportA,
                            arrival = airportB,
                            departureAt = LocalDateTime.ofInstant(departureAtInstant, UTC),
                            arrivalAt = LocalDateTime.ofInstant(returnAtInstant, UTC),
                        ),
                        Flight(
                            price = Money.of(32.5, "USD"),
                            airline = AirlineName("Turkish Airlines"),
                            departure = airportA,
                            arrival = airportB,
                            departureAt = LocalDateTime.ofInstant(departureAtInstant, UTC),
                            arrivalAt = LocalDateTime.ofInstant(returnAtInstant, UTC),
                        ),
                    )
                }

                server.verify(
                    postRequestedFor(urlPathEqualTo("/flights"))
                        .withHeader("Authorization", equalTo("Bearer $key"))
                        .withHeader("Content-Type", equalTo("application/json"))
                        .withRequestBody(equalToJson(requestJson)),
                )
            }
        }
    }

    Given("Server replies with server error") {
        server.stubFor(
            post(urlPathEqualTo("/flights"))
                .willReturn(
                    aResponse()
                        .withStatus(500)
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                            """{"error": "Internal server error"}""",
                        ),
                ),
        )

        When("Client requests list of flights") {
            val result = client.search(request)

            Then("Client receives error") {
                result.shouldBeLeft().asClue {
                    it.shouldBeInstanceOf<ProviderUnavailable>()
                    it.message shouldBe """Tough Jet provider is unavailable: {"error": "Internal server error"}"""
                    it.provider shouldBe TOUGH_JET
                }

                server.verify(
                    postRequestedFor(urlPathEqualTo("/flights"))
                        .withHeader("Authorization", equalTo("Bearer $key"))
                        .withHeader("Content-Type", equalTo("application/json"))
                        .withRequestBody(equalToJson(requestJson)),
                )
            }
        }
    }

    Given("Server replies with invalid request error") {
        server.stubFor(
            post(urlPathEqualTo("/flights"))
                .willReturn(
                    aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                            """{"error": "Invalid request"}""",
                        ),
                ),
        )

        When("Client requests list of flights") {
            val result = client.search(request)

            Then("Client receives error") {
                result.shouldBeLeft().asClue {
                    it.shouldBeInstanceOf<InvalidRequest>()
                    it.message shouldBe """Tough Jet request failed: {"error": "Invalid request"}"""
                    it.provider shouldBe TOUGH_JET
                }

                server.verify(
                    postRequestedFor(urlPathEqualTo("/flights"))
                        .withHeader("Authorization", equalTo("Bearer $key"))
                        .withHeader("Content-Type", equalTo("application/json"))
                        .withRequestBody(equalToJson(requestJson)),
                )
            }
        }
    }

    Given("Server is not replying in time") {
        server.stubFor(
            post(urlPathMatching("/flights"))
                .willReturn(
                    aResponse()
                        .withBody(
                            """
                            {
                                "results": []
                            }
                            """.trimIndent(),
                        )
                        .withStatus(200)
                        .withFixedDelay(5000),
                ),
        )

        When("Client requests list of flights") {
            val result = client.search(request)

            Then("Client receives timeout error") {
                result.shouldBeLeft().asClue {
                    it.shouldBeInstanceOf<RequestTimeout>()
                    it.cause.shouldBeInstanceOf<HttpRequestTimeoutException>()
                    it.message shouldBe "Tough Jet didn't reply in time"
                    it.provider shouldBe TOUGH_JET
                }

                server.verify(
                    postRequestedFor(urlPathEqualTo("/flights"))
                        .withHeader("Authorization", equalTo("Bearer $key"))
                        .withHeader("Content-Type", equalTo("application/json"))
                        .withRequestBody(equalToJson(requestJson)),
                )
            }
        }
    }
})
