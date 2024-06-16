package de.maltsev.deblock.integration.flights.toughjet

import de.maltsev.deblock.flights.AirlineName
import de.maltsev.deblock.flights.AirportCode
import de.maltsev.deblock.flights.Flight
import de.maltsev.deblock.integration.flights.provider.FlightsSearchRequest
import de.maltsev.deblock.integration.flights.provider.FlightsSearchResult
import de.maltsev.deblock.integration.flights.toughjet.ToughJetTotalPriceProvider.FlightPriceDetails
import de.maltsev.deblock.integration.flights.toughjet.ToughJetTotalPriceProvider.totalPrice
import de.maltsev.deblock.json.int
import de.maltsev.deblock.json.objectArray
import de.maltsev.deblock.json.string
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
        put("outboundDate", departureDate.toString())
        put("inboundDate", returnDate.toString())
        put("numberOfAdults", numberOfPassengers.total().toInt())
    }

    internal fun JsonObject.toResponse() = FlightsSearchResult(
        flights = objectArray("flights").map { it.toCommonFlight() },
    )

    private fun JsonObject.toCommonFlight() = Flight(
        airline = string("carrier").let(::AirlineName),
        price = FlightPriceDetails(
            basePrice = string("basePrice").let(Money::parse),
            tax = string("tax").let(Money::parse),
            discount = int("discount").let(Percentage::of),
        ).totalPrice(),
        departureFrom = string("departureAirportName").let(::AirportCode),
        arrivalTo = string("arrivalAirportName").let(::AirportCode),
        departureAt = string("outboundDateTime").let(Instant::parse).let(::utcLocalDateTime),
        arrivalAt = string("inboundDateTime").let(Instant::parse).let(::utcLocalDateTime),
    )

    private fun utcLocalDateTime(instant: Instant): LocalDateTime = ofInstant(instant, UTC)
}
