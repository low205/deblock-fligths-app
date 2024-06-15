package de.maltsev.deblock.integration.flights.crazyair

import de.maltsev.deblock.flights.AirlineName
import de.maltsev.deblock.flights.AirportCode
import de.maltsev.deblock.flights.Flight
import de.maltsev.deblock.integration.flights.provider.FlightsSearchRequest
import de.maltsev.deblock.integration.flights.provider.FlightsSearchResult
import de.maltsev.deblock.json.asJsonObjectArray
import de.maltsev.deblock.json.asString
import de.maltsev.deblock.json.get
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.javamoney.moneta.Money
import java.time.LocalDateTime

internal object CrazyAirClientMapper {

    internal fun FlightsSearchRequest.toRequest(): JsonObject = buildJsonObject {
        put("origin", origin.value)
        put("destination", destination.value)
        put("departureDate", departureAt.toString())
        put("returnDate", returnAt.toString())
        put("passengerCount", passengerCount.total().toInt())
    }

    internal fun JsonObject.toResponse() = FlightsSearchResult(
        flights = get("results", asJsonObjectArray).map { it.toCommonFlight() },
    )

    private fun JsonObject.toCommonFlight() = Flight(
        airline = get("airline", asString).let(::AirlineName),
        price = get("price", asString).let(Money::parse),
        departure = get("departureAirportCode", asString).let(::AirportCode),
        arrival = get("destinationAirportCode", asString).let(::AirportCode),
        departureAt = get("departureDate", asString).let(LocalDateTime::parse),
        arrivalAt = get("arrivalDate", asString).let(LocalDateTime::parse),
    )
}
