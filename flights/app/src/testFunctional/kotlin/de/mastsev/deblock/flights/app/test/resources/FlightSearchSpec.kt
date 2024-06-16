package de.mastsev.deblock.flights.app.test.resources

import de.maltsev.deblock.flights.CabinClass.BUSINESS
import de.maltsev.deblock.flights.CabinClass.ECONOMY
import de.maltsev.deblock.fligts.test.TestData.aFlight
import de.maltsev.deblock.fligts.test.TestData.anAirlineName
import de.mastsev.deblock.flights.app.test.ApplicationSpec
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.ktor.client.request.basicAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType.Application.Json
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import io.ktor.http.contentType
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.javamoney.moneta.Money
import java.time.ZoneOffset.UTC

class FlightSearchSpec : ApplicationSpec({

    Given("Providers return no flights") {
        app.crazyAirServer.givenNoFlights()
        app.toughJetServer.givenNoFlights()

        When("GET /flights with search parameters") {
            val response = client.post("/search-flights") {
                contentType(Json)
                basicAuth(app.user, app.password)
                setBody(
                    buildJsonObject {
                        put("origin", "FRA")
                        put("destination", "LAX")
                        put("departureDate", "2024-06-01")
                        put("returnDate", "2024-06-02")
                        put("numberOfPassengers", 4)
                    },
                )
            }

            Then("Server responds with 200 and no flights") {
                response shouldHaveStatus OK
                response.bodyAsText() shouldEqualJson """
                    {
                        "flights": []
                    }
                """.trimIndent()
            }
        }

        When("GET /flights with too many passengers") {
            val response = client.post("/search-flights") {
                contentType(Json)
                basicAuth(app.user, app.password)
                setBody(
                    buildJsonObject {
                        put("origin", "FRA")
                        put("destination", "LAX")
                        put("departureDate", "2024-06-01")
                        put("returnDate", "2024-06-02")
                        put("numberOfPassengers", 5)
                    },
                )
            }

            Then("Server responds with 400") {
                response shouldHaveStatus BadRequest
                response.bodyAsText() shouldEqualJson """
                    {
                       "error": {
                           "message": "Maximum 4 passengers are allowed"
                       }
                    }
                """.trimIndent()
            }
        }

        When("GET /flights with invalid dates") {
            val response = client.post("/search-flights") {
                contentType(Json)
                basicAuth(app.user, app.password)
                setBody(
                    buildJsonObject {
                        put("origin", "FRA")
                        put("destination", "LAX")
                        put("departureDate", "2024-06-01")
                        put("returnDate", "2024-05-31")
                        put("numberOfPassengers", 4)
                    },
                )
            }

            Then("Server responds with 400") {
                response shouldHaveStatus BadRequest
                response.bodyAsText() shouldEqualJson """
                    {
                       "error": {
                           "message": "Departure date must be before return date"
                       }
                    }
                """.trimIndent()
            }
        }

        When("User is not authenticated") {
            val response = client.post("/search-flights") {
                contentType(Json)
                setBody(
                    buildJsonObject {
                        put("origin", "FRA")
                        put("destination", "LAX")
                        put("departureDate", "2024-06-01")
                        put("returnDate", "2024-06-02")
                        put("numberOfPassengers", 4)
                    },
                )
            }

            Then("Server responds with 401") {
                response shouldHaveStatus Unauthorized
            }
        }
    }

    Given("Providers return flights") {
        val flightA = aFlight(
            price = Money.of(100.0, "EUR"),
        )
        val flightB = flightA.copy(
            airline = anAirlineName(),
            price = Money.of(99.0, "EUR"),
        )
        val flightC = flightA.copy(
            airline = anAirlineName(),
            price = Money.of(50.0, "EUR"),
        )
        val flightD = flightA.copy(
            airline = anAirlineName(),
            price = Money.of(110.0, "EUR"),
        )
        app.crazyAirServer.givenFlights(
            airline = flightA.airline,
            price = flightA.price,
            cabinclass = ECONOMY,
            departureAirportCode = flightA.departureFrom,
            destinationAirportCode = flightA.arrivalTo,
            departureDate = flightA.departureAt,
            arrivalDate = flightA.arrivalAt,
        )
        app.crazyAirServer.givenFlights(
            airline = flightB.airline,
            price = flightB.price,
            cabinclass = BUSINESS,
            departureAirportCode = flightB.departureFrom,
            destinationAirportCode = flightB.arrivalTo,
            departureDate = flightB.departureAt,
            arrivalDate = flightB.arrivalAt,
        )
        app.toughJetServer.givenFlight(
            carrier = flightC.airline,
            basePrice = flightC.price,
            tax = Money.of(5.0, "EUR"),
            discount = 10u,
            departureAirportName = flightC.departureFrom,
            arrivalAirportName = flightC.arrivalTo,
            outboundDateTime = flightC.departureAt.toInstant(UTC),
            inboundDateTime = flightC.arrivalAt.toInstant(UTC),
        )
        app.toughJetServer.givenFlight(
            carrier = flightD.airline,
            basePrice = flightD.price,
            tax = Money.of(5.0, "EUR"),
            discount = 0u,
            departureAirportName = flightD.departureFrom,
            arrivalAirportName = flightD.arrivalTo,
            outboundDateTime = flightD.departureAt.toInstant(UTC),
            inboundDateTime = flightD.arrivalAt.toInstant(UTC),
        )

        When("GET /flights with search parameters") {
            val response = client.post("/search-flights") {
                contentType(Json)
                basicAuth(app.user, app.password)
                setBody(
                    buildJsonObject {
                        put("origin", flightA.departureFrom.value)
                        put("destination", flightA.arrivalTo.value)
                        put("departureDate", flightA.departureAt.toLocalDate().toString())
                        put("returnDate", flightA.arrivalAt.toLocalDate().toString())
                        put("numberOfPassengers", 1)
                    },
                )
            }

            Then("Server responds with 200 and flights") {
                response.bodyAsText() shouldEqualJson """
                    {
                        "flights": [
                            {
                              "supplier": "ToughJet",
                              "airline": "${flightC.airline.name}",
                              "fare": "EUR 50",
                              "departureAirportCode": "${flightC.departureFrom.value}",
                              "destinationAirportCode": "${flightC.arrivalTo.value}",
                              "departureDate": "${flightC.departureAt}",
                              "arrivalDate": "${flightC.arrivalAt}"
                            },
                            {
                              "supplier": "CrazyAir",
                              "airline": "${flightB.airline.name}",
                              "fare": "EUR 99",
                              "departureAirportCode": "${flightB.departureFrom.value}",
                              "destinationAirportCode": "${flightB.arrivalTo.value}",
                              "departureDate": "${flightB.departureAt}",
                              "arrivalDate": "${flightB.arrivalAt}"
                            },
                            {
                              "supplier": "CrazyAir",
                              "airline": "${flightA.airline.name}",
                              "fare": "EUR 100",
                              "departureAirportCode": "${flightA.departureFrom.value}",
                              "destinationAirportCode": "${flightA.arrivalTo.value}",
                              "departureDate": "${flightA.departureAt}",
                              "arrivalDate": "${flightA.arrivalAt}"
                            },
                            {
                              "supplier": "ToughJet",
                              "airline": "${flightD.airline.name}",
                              "fare": "EUR 115",
                              "departureAirportCode": "${flightD.departureFrom.value}",
                              "destinationAirportCode": "${flightD.arrivalTo.value}",
                              "departureDate": "${flightD.departureAt}",
                              "arrivalDate": "${flightD.arrivalAt}"
                            }
                        ]
                    }
                """.trimIndent()
                response shouldHaveStatus OK
            }
        }
    }
})
