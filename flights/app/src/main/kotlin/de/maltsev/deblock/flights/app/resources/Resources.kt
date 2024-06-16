package de.maltsev.deblock.flights.app.resources

import io.ktor.resources.Resource

object Resources {

    @Resource("/health")
    object HealthCheck

    @Resource("/search-flights")
    object SearchFlights
}
