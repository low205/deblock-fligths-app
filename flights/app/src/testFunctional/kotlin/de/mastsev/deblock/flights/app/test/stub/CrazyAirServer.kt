package de.mastsev.deblock.flights.app.test.stub

import de.maltsev.deblock.flights.AirlineName
import de.maltsev.deblock.flights.AirportCode
import de.maltsev.deblock.flights.CabinClass
import de.maltsev.deblock.flights.CabinClass.BUSINESS
import de.maltsev.deblock.flights.CabinClass.ECONOMY
import de.maltsev.deblock.flights.app.server.response.respondJson
import de.maltsev.deblock.json.localDate
import de.maltsev.deblock.json.localDateTime
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
import java.time.LocalDateTime
import java.util.concurrent.CopyOnWriteArrayList

class CrazyAirServer : TestServer() {

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
            post("/search") {
                val request = call.receive<JsonObject>()
                val flights = store.filter { stored ->
                    stored.string("departureAirportCode") == request.string("origin") &&
                        stored.string("destinationAirportCode") == request.string("destination") &&
                        stored.localDateTime("departureDate").toLocalDate() == request.localDate("departureDate") &&
                        stored.localDateTime("arrivalDate").toLocalDate() == request.localDate("returnDate")
                }
                call.respondJson {
                    putJsonArray("results") {
                        flights.forEach { add(it) }
                    }
                }
            }
        }
    }

    fun givenNoFlights() {
        store.clear()
    }

    fun givenFlights(
        airline: AirlineName,
        price: Money,
        cabinclass: CabinClass,
        departureAirportCode: AirportCode,
        destinationAirportCode: AirportCode,
        departureDate: LocalDateTime,
        arrivalDate: LocalDateTime,
    ) {
        val flight = buildJsonObject {
            put("airline", airline.name)
            put("price", price.toString())
            put(
                "cabinclass",
                when (cabinclass) {
                    ECONOMY -> "E"
                    BUSINESS -> "B"
                },
            )
            put("departureAirportCode", departureAirportCode.value)
            put("destinationAirportCode", destinationAirportCode.value)
            put("departureDate", departureDate.toString())
            put("arrivalDate", arrivalDate.toString())
        }
        store.add(flight)
    }
}
