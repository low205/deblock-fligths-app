package de.maltsev.deblock.integration.flights.provider

import arrow.core.Either
import de.maltsev.deblock.flights.FlightProvider

interface FlightListProvider {

    val provider: FlightProvider

    suspend fun search(request: FlightsSearchRequest): Either<FlightProviderError, FlightsSearchResult>
}
