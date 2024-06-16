package de.maltsev.deblock.flights.app.providers

import arrow.core.left
import arrow.core.right
import de.maltsev.deblock.flights.FlightProvider.CRAZY_AIR
import de.maltsev.deblock.flights.FlightProvider.TOUGH_JET
import de.maltsev.deblock.fligts.test.TestData.aFlight
import de.maltsev.deblock.fligts.test.TestData.anAirportCode
import de.maltsev.deblock.integration.flights.provider.FlightListProvider
import de.maltsev.deblock.integration.flights.provider.FlightProviderError
import de.maltsev.deblock.integration.flights.provider.FlightsSearchRequest
import de.maltsev.deblock.integration.flights.provider.FlightsSearchResult
import de.maltsev.deblock.integration.flights.provider.PassengerCount
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.localDate
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.uInt
import io.mockk.coEvery
import io.mockk.coVerifyAll
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDate

class FlightsAggregationProviderSpec : BehaviorSpec({

    Given("Several Search Providers") {
        val aSearchRequest = FlightsSearchRequest(
            origin = anAirportCode(),
            destination = anAirportCode(),
            departureDate = Arb.localDate(maxDate = LocalDate.now()).next(),
            returnDate = Arb.localDate(minDate = LocalDate.now()).next(),
            numberOfPassengers = PassengerCount(
                adults = Arb.uInt(1u..4u).next(),
            ),
        )
        val provider1 = mockk<FlightListProvider> {
            every { provider } returns TOUGH_JET
        }
        val provider2 = mockk<FlightListProvider> {
            every { provider } returns CRAZY_AIR
        }

        val provider = FlightsAggregationProvider(
            providers = listOf(
                provider1,
                provider2,
            ),
        )

        And("Providers return no flights") {
            coEvery { provider1.search(aSearchRequest) } returns FlightsSearchResult(emptyList()).right()
            coEvery { provider2.search(aSearchRequest) } returns FlightsSearchResult(emptyList()).right()

            When("Searching Flights") {
                val result = provider.searchFlights(aSearchRequest)

                Then("No flights are returned") {
                    result.flights.size shouldBe 0
                    coVerifyAll {
                        provider1.provider
                        provider2.provider
                        provider1.search(aSearchRequest)
                        provider2.search(aSearchRequest)
                    }
                }
            }
        }

        And("Providers return some flights") {
            val flights1 = listOf(
                aFlight(),
                aFlight(),
            )
            val flights2 = listOf(
                aFlight(),
                aFlight(),
            )
            coEvery { provider1.search(aSearchRequest) } returns FlightsSearchResult(flights1).right()
            coEvery { provider2.search(aSearchRequest) } returns FlightsSearchResult(flights2).right()

            When("Searching Flights") {
                val result = provider.searchFlights(aSearchRequest)

                Then("All flights are returned") {
                    result.flights.size shouldBe 4
                    result.flights shouldContainAll flights1.map {
                        AggregatedFlight(
                            provider = TOUGH_JET,
                            flight = it,
                        )
                    }
                    result.flights shouldContainAll flights2.map {
                        AggregatedFlight(
                            provider = CRAZY_AIR,
                            flight = it,
                        )
                    }
                    result.flights
                        .map { it.flight.price }.sorted() shouldBe (flights1 + flights2).map { it.price }.sorted()
                    coVerifyAll {
                        provider1.provider
                        provider2.provider
                        provider1.search(aSearchRequest)
                        provider2.search(aSearchRequest)
                    }
                }
            }
        }

        And("One provider fails") {
            val flight = aFlight()
            coEvery { provider1.search(aSearchRequest) } returns FlightsSearchResult(listOf(flight)).right()
            coEvery { provider2.search(aSearchRequest) } returns FlightProviderError.ProviderUnavailable(
                provider = CRAZY_AIR,
                message = "Service is down",
            ).left()

            When("Searching Flights") {
                val result = provider.searchFlights(aSearchRequest)

                Then("Only successful provider flights are returned") {
                    result.flights.size shouldBe 1
                    result.flights shouldContainExactly listOf(
                        AggregatedFlight(
                            provider = TOUGH_JET,
                            flight = flight,
                        ),
                    )
                    coVerifyAll {
                        provider1.provider
                        provider2.provider
                        provider1.search(aSearchRequest)
                        provider2.search(aSearchRequest)
                    }
                }
            }
        }
    }
})
