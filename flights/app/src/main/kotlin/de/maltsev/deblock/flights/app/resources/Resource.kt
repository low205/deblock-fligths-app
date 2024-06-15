package de.maltsev.deblock.flights.app.resources

import io.ktor.server.routing.Routing

interface Resource {

    fun Routing.register()
}
