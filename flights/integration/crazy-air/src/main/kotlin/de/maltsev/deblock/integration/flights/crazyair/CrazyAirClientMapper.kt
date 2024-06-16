package de.maltsev.deblock.integration.flights.crazyair

import de.maltsev.deblock.flights.AirlineName
import de.maltsev.deblock.flights.AirportCode
import de.maltsev.deblock.flights.Flight
import de.maltsev.deblock.integration.flights.provider.FlightsSearchRequest
import de.maltsev.deblock.integration.flights.provider.FlightsSearchResult
import de.maltsev.deblock.json.objectArray
import de.maltsev.deblock.json.string
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.javamoney.moneta.Money
import java.time.LocalDateTime

internal object CrazyAirClientMapper {

    internal fun FlightsSearchRequest.toRequest(): JsonObject = buildJsonObject {
        put("origin", origin.value)
        put("destination", destination.value)
        put("departureDate", departureDate.toString())
        put("returnDate", returnDate.toString())
        put("passengerCount", numberOfPassengers.total().toInt())
    }

    internal fun JsonObject.toResponse() = FlightsSearchResult(
        flights = objectArray("results").map { it.toCommonFlight() },
    )

    private fun JsonObject.toCommonFlight() = Flight(
        airline = string("airline").let(::AirlineName),
        price = string("price").let(Money::parse),
        departureFrom = string("departureAirportCode").let(::AirportCode),
        arrivalTo = string("destinationAirportCode").let(::AirportCode),
        departureAt = string("departureDate").let(LocalDateTime::parse),
        arrivalAt = string("arrivalDate").let(LocalDateTime::parse),
    )
}
