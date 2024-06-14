package de.maltsev.deblock.integration.flights.provider

import arrow.core.Either

interface FlightListProvider {

    suspend fun search(request: FlightsSearchRequest): Either<FlightProviderError, FlightsSearchResult>
}
