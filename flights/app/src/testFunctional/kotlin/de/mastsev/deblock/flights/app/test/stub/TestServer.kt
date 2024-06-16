package de.mastsev.deblock.flights.app.test.stub

import de.mastsev.deblock.flights.app.test.randomPort
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.auth.AuthenticationConfig
import io.ktor.server.auth.authentication
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.routing.Routing
import io.ktor.server.routing.routing
import java.lang.Runtime.getRuntime

open class TestServer {

    val port: Int = randomPort()

    open fun AuthenticationConfig.configure() {
    }

    open fun Routing.configure() {
    }

    init {
        embeddedServer(
            factory = Netty,
            port = port,
            host = "localhost",
        ) {
            install(CallLogging)
            install(DefaultHeaders)
            install(ContentNegotiation) {
                json()
            }
            authentication {
                configure()
            }
            routing {
                configure()
            }
        }.also {
            it.start()
            getRuntime().addShutdownHook(
                Thread {
                    it.stop(gracePeriodMillis = 0, timeoutMillis = 0)
                },
            )
        }
    }
}
