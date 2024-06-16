package de.maltsev.deblock.flights.app.resources

import de.maltsev.deblock.flights.AirportCode
import de.maltsev.deblock.flights.app.providers.AggregatedFlight
import de.maltsev.deblock.flights.app.providers.FlightsAggregation
import de.maltsev.deblock.integration.flights.provider.FlightsSearchRequest
import de.maltsev.deblock.integration.flights.provider.PassengerCount
import de.maltsev.deblock.json.localDate
import de.maltsev.deblock.json.string
import de.maltsev.deblock.json.uInt
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray

internal object SearchFlightsResourceMapper {

    internal suspend fun ApplicationCall.receiveSearchFlightsRequest() = receive<JsonObject>().toSearchFlightsRequest()

    private fun JsonObject.toSearchFlightsRequest() = FlightsSearchRequest(
        origin = string("origin").let(::AirportCode),
        destination = string("destination").let(::AirportCode),
        departureDate = localDate("departureDate"),
        returnDate = localDate("returnDate"),
        numberOfPassengers = PassengerCount(
            adults = uInt("numberOfPassengers"),
        ),
    )

    internal fun FlightsAggregation.toResponse() = buildJsonObject {
        putJsonArray("flights") {
            flights.forEach {
                add(it.toJson())
            }
        }
    }

    private fun AggregatedFlight.toJson() = buildJsonObject {
        put("supplier", provider.displayName)
        with(flight) {
            put("airline", airline.name)
            put("fare", price.toString())
            put("departureAirportCode", departureFrom.value)
            put("destinationAirportCode", arrivalTo.value)
            put("departureDate", departureAt.toString())
            put("arrivalDate", arrivalAt.toString())
        }
    }
}
