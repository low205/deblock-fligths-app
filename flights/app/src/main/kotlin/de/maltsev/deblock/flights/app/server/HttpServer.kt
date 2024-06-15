package de.maltsev.deblock.flights.app.server

import de.maltsev.deblock.flights.app.config.ServerConfig
import de.maltsev.deblock.flights.app.resources.Resource
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

class HttpServer(
    private val serverConfig: ServerConfig,
    private val resources: List<Resource>,
) {

    private val server = embeddedServer(
        factory = Netty,
        port = serverConfig.port,
    ) {
        module(
            resources = resources,
            basicAuthCredentials = serverConfig.auth.basic,
        )
    }

    fun start(wait: Boolean) {
        server.start(wait = wait)
    }

    fun stop() {
        server.stop(
            gracePeriodMillis = 1_000,
            timeoutMillis = 1_000,
        )
    }
}
