package de.maltsev.deblock.flights.app.resources

import de.maltsev.deblock.flights.app.providers.FlightsAggregationProvider
import de.maltsev.deblock.flights.app.resources.Resources.SearchFlights
import de.maltsev.deblock.flights.app.resources.SearchFlightsResourceMapper.receiveSearchFlightsRequest
import de.maltsev.deblock.flights.app.resources.SearchFlightsResourceMapper.toResponse
import de.maltsev.deblock.flights.app.server.auth.BasicAuthentication.basicAuth
import io.ktor.server.application.call
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing

class SearchFlightsResource(
    private val flightsProvider: FlightsAggregationProvider,
) : Resource {

    override fun Routing.register() {
        basicAuth {
            post<SearchFlights> {
                searchFlights()
            }
        }
    }

    private suspend fun CallPipeline.searchFlights() = flightsProvider
        .searchFlights(call.receiveSearchFlightsRequest())
        .toResponse()
        .also {
            call.respond(it)
        }
}
