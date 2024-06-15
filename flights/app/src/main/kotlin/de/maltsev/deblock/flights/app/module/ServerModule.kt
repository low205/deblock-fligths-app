package de.maltsev.deblock.flights.app.module

import de.maltsev.deblock.flights.app.config.ServerConfig
import de.maltsev.deblock.flights.app.resources.Resource
import de.maltsev.deblock.flights.app.server.HttpServer

class ServerModule(
    serverConfig: ServerConfig,
    resources: List<Resource>,
) {

    private val server = HttpServer(serverConfig, resources)

    fun start(wait: Boolean) {
        server.start(wait)
    }

    fun stop() {
        server.stop()
    }
}
