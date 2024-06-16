package de.maltsev.deblock.flights.app.providers

import de.maltsev.deblock.integration.flights.provider.FlightListProvider
import de.maltsev.deblock.integration.flights.provider.FlightsSearchRequest
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope
import mu.KotlinLogging.logger

class FlightsAggregationProvider(
    providers: List<FlightListProvider>,
) {

    private val log = logger {}
    private val providers = providers.associateBy { it.provider }

    suspend fun searchFlights(searchFlights: FlightsSearchRequest): FlightsAggregation = supervisorScope {
        FlightsAggregation(
            flights = providers.map { (providerName, provider) ->
                async {
                    provider
                        .search(searchFlights)
                        .onLeft {
                            log.error(it.cause) { "Failed to search flights from $providerName, error=$it" }
                        }
                        .map {
                            it.flights.map { flight ->
                                AggregatedFlight(
                                    provider = providerName,
                                    flight = flight,
                                )
                            }
                        }
                        .getOrNull()
                        .orEmpty()
                }
            }.awaitAll().asSequence().flatten().sortedBy { it.flight.price }.toList(),
        )
    }
}
