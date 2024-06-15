package de.mastsev.deblock.flights.app.test.stub

import de.maltsev.deblock.flights.app.server.response.respondJson
import io.kotest.property.Arb
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.alphanumeric
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string
import io.ktor.server.application.call
import io.ktor.server.auth.AuthenticationConfig
import io.ktor.server.auth.bearer
import io.ktor.server.request.receive
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.putJsonArray
import java.util.concurrent.ConcurrentHashMap

class CrazyAirServer : TestServer() {

    private val flightsRequests = ConcurrentHashMap<JsonObject, List<JsonObject>>()

    val apiToken = Arb.string(36, Codepoint.alphanumeric()).next()

    override fun AuthenticationConfig.configure() {
        bearer {
            this.authenticate {
                when (it.token) {
                    apiToken -> TestPrincipal
                    else -> null
                }
            }
        }
    }

    override fun Routing.configure() {
        get("/search") {
            val request = call.receive<JsonObject>()
            val response = flightsRequests[request].orEmpty()
            call.respondJson {
                buildJsonObject {
                    putJsonArray("results") {
                        response.forEach { add(it) }
                    }
                }
            }
        }
    }
}
