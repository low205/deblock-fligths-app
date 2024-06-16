package de.mastsev.deblock.flights.app.test

import de.maltsev.deblock.flights.app.application
import de.maltsev.deblock.http.client.HttpClients.createHttpClient
import de.maltsev.deblock.json.findString
import de.mastsev.deblock.flights.app.test.stub.CrazyAirServer
import de.mastsev.deblock.flights.app.test.stub.ToughJetServer
import io.kotest.assertions.asClue
import io.kotest.assertions.until.until
import io.kotest.matchers.nulls.shouldNotBeNull
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode.Companion.OK
import kotlinx.serialization.json.JsonObject
import org.apache.commons.text.StringSubstitutor
import java.io.File
import java.io.File.createTempFile
import java.lang.Runtime.getRuntime
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds

class TestFlightsApp {

    val user: String = "test"
    val password: String = "pass"
    val port = randomPort()
    val crazyAirServer = CrazyAirServer()
    val toughJetServer = ToughJetServer()
    val configFile: File = TestFlightsApp::class.java.getResource("/test-config.yml")
        .shouldNotBeNull()
        .readText()
        .let { configContent ->
            StringSubstitutor(
                mapOf(
                    "port" to port,
                    "username" to user,
                    "password" to password,
                    "toughJetUrl" to "http://localhost:${toughJetServer.port}",
                    "toughJetApiKey" to toughJetServer.apiToken,
                    "crazyAirUrl" to "http://localhost:${crazyAirServer.port}",
                    "crazyAirApiKey" to crazyAirServer.apiToken,
                ),
            ).replace(configContent)
        }
        .let { config ->
            createTempFile("config", ".yml").also {
                it.writeText(config)
            }
        }

    val app = application(configFile).also {
        it.start(wait = false)
    }

    val client = createHttpClient(
        baseUrl = "http://localhost:$port",
        maxConnectionsCount = 1,
        maxConnectionsPerRoute = 1,
        requestTimeout = 1.days,
    )

    suspend fun awaitStart() {
        "Server should start and respond to health check".asClue {
            until(5.seconds) {
                client.get("/health").run {
                    status == OK && body<JsonObject>().findString("status") == "OK"
                }
            }
        }
    }

    init {
        getRuntime().addShutdownHook(Thread { app.stop() })
    }
}
