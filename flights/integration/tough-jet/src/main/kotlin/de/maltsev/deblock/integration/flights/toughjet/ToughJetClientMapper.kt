package de.maltsev.deblock.integration.flights.toughjet

import de.maltsev.deblock.flights.AirlineName
import de.maltsev.deblock.flights.AirportCode
import de.maltsev.deblock.flights.Flight
import de.maltsev.deblock.integration.flights.provider.FlightsSearchRequest
import de.maltsev.deblock.integration.flights.provider.FlightsSearchResult
import de.maltsev.deblock.integration.flights.toughjet.ToughJetTotalPriceProvider.FlightPriceDetails
import de.maltsev.deblock.integration.flights.toughjet.ToughJetTotalPriceProvider.totalPrice
import de.maltsev.deblock.json.asInt
import de.maltsev.deblock.json.asJsonObjectArray
import de.maltsev.deblock.json.asString
import de.maltsev.deblock.json.get
import de.maltsev.deblock.math.Percentage
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.javamoney.moneta.Money
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalDateTime.ofInstant
import java.time.ZoneOffset.UTC

internal object ToughJetClientMapper {

    internal fun FlightsSearchRequest.toRequest(): JsonObject = buildJsonObject {
        put("from", origin.value)
        put("to", destination.value)
        put("outboundDate", departureAt.toString())
        put("inboundDate", returnAt.toString())
        put("numberOfAdults", passengerCount.total().toInt())
    }

    internal fun JsonObject.toResponse() = FlightsSearchResult(
        flights = get("flights", asJsonObjectArray).map { it.toCommonFlight() },
    )

    private fun JsonObject.toCommonFlight() = Flight(
        airline = get("carrier", asString).let(::AirlineName),
        price = FlightPriceDetails(
            basePrice = get("basePrice", asString).let(Money::parse),
            tax = get("tax", asString).let(Money::parse),
            discount = get("discount", asInt).let(Percentage::of),
        ).totalPrice(),
        departure = get("departureAirportName", asString).let(::AirportCode),
        arrival = get("arrivalAirportName", asString).let(::AirportCode),
        departureAt = get("outboundDateTime", asString).let(Instant::parse).let(::utcLocalDateTime),
        arrivalAt = get("inboundDateTime", asString).let(Instant::parse).let(::utcLocalDateTime),
    )

    private fun utcLocalDateTime(instant: Instant): LocalDateTime = ofInstant(instant, UTC)
}
