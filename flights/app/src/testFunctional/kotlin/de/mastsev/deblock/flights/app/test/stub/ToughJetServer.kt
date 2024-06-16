package de.mastsev.deblock.flights.app.test.stub

import de.maltsev.deblock.flights.AirlineName
import de.maltsev.deblock.flights.AirportCode
import de.maltsev.deblock.flights.app.server.response.respondJson
import de.maltsev.deblock.json.instant
import de.maltsev.deblock.json.localDate
import de.maltsev.deblock.json.string
import io.kotest.property.Arb
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.alphanumeric
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string
import io.ktor.server.application.call
import io.ktor.server.auth.AuthenticationConfig
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.bearer
import io.ktor.server.request.receive
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import org.javamoney.moneta.Money
import java.time.Instant
import java.time.ZoneOffset.UTC
import java.util.concurrent.CopyOnWriteArrayList

class ToughJetServer : TestServer() {

    private val store = CopyOnWriteArrayList<JsonObject>()

    val apiToken = Arb.string(36, Codepoint.alphanumeric()).next()

    override fun AuthenticationConfig.configure() {
        bearer("auth") {
            authenticate {
                when (it.token) {
                    apiToken -> TestPrincipal
                    else -> null
                }
            }
        }
    }

    override fun Routing.configure() {
        authenticate("auth") {
            post("/flights") {
                val request = call.receive<JsonObject>()
                val response = store.filter { stored ->
                    stored.string("departureAirportName") == request.string("from") &&
                        stored.string("arrivalAirportName") == request.string("to") &&
                        stored.instant("outboundDateTime").atZone(UTC)
                            .toLocalDate() == request.localDate("outboundDate") &&
                        stored.instant("inboundDateTime").atZone(UTC).toLocalDate() == request.localDate("inboundDate")
                }
                call.respondJson {
                    putJsonArray("flights") {
                        response.forEach { add(it) }
                    }
                }
            }
        }
    }

    fun givenNoFlights() {
        store.clear()
    }

    fun givenFlight(
        carrier: AirlineName,
        basePrice: Money,
        tax: Money,
        discount: UInt,
        departureAirportName: AirportCode,
        arrivalAirportName: AirportCode,
        outboundDateTime: Instant,
        inboundDateTime: Instant,
    ) {
        val flight = buildJsonObject {
            put("carrier", carrier.name)
            put("basePrice", basePrice.toString())
            put("tax", tax.toString())
            put("discount", discount.toInt())
            put("departureAirportName", departureAirportName.value)
            put("arrivalAirportName", arrivalAirportName.value)
            put("outboundDateTime", outboundDateTime.toString())
            put("inboundDateTime", inboundDateTime.toString())
        }
        store.add(flight)
    }
}
