package de.maltsev.deblock.fligts.test

import de.maltsev.deblock.flights.AirlineName
import de.maltsev.deblock.flights.AirportCode
import de.maltsev.deblock.flights.Flight
import io.kotest.property.Arb
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.filterNot
import io.kotest.property.arbitrary.localDateTime
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.of
import org.javamoney.moneta.Money
import java.time.LocalDateTime

object TestData {

    val knownAirlines = listOf(
        AirlineName("Lufthansa"),
        AirlineName("Aeroflot"),
        AirlineName("Emirates"),
        AirlineName("Qatar Airways"),
        AirlineName("Singapore Airlines"),
        AirlineName("Cathay Pacific"),
        AirlineName("ANA"),
        AirlineName("Etihad Airways"),
        AirlineName("Qantas"),
        AirlineName("Turkish Airlines"),
        AirlineName("EVA Air"),
        AirlineName("Garuda Indonesia"),
        AirlineName("Thai Airways"),
        AirlineName("Japan Airlines"),
        AirlineName("Swiss International Air Lines"),
        AirlineName("Austrian Airlines"),
        AirlineName("Air New Zealand"),
    )

    val knowAirports = listOf(
        AirportCode("FRA"),
        AirportCode("LAX"),
        AirportCode("JFK"),
        AirportCode("CDG"),
        AirportCode("LHR"),
        AirportCode("HND"),
        AirportCode("HKG"),
        AirportCode("AMS"),
        AirportCode("SIN"),
        AirportCode("DXB"),
        AirportCode("ICN"),
        AirportCode("CAN"),
        AirportCode("MUC"),
        AirportCode("BKK"),
        AirportCode("DEL"),
        AirportCode("PEK"),
        AirportCode("MAD"),
        AirportCode("BCN"),
        AirportCode("IST"),
        AirportCode("SYD"),
    )

    fun anAirlineName() = Arb.of(knownAirlines).next()

    val knownCurrencies = listOf(
        "USD",
        "EUR",
        "JPY",
        "GBP",
        "AUD",
        "CAD",
        "CHF",
        "CNY",
    )

    fun anAmount(): Money = Money.of(
        Arb.double(0.01..10_000.0).next(),
        Arb.of(knownCurrencies).next(),
    )

    fun anAirportCode(except: AirportCode? = null) = Arb.of(knowAirports)
        .filterNot { it == except }
        .next()

    fun aFlight(
        money: Money = anAmount(),
        airline: AirlineName = anAirlineName(),
        departure: AirportCode = anAirportCode(),
        arrival: AirportCode = anAirportCode(except = departure),
        departureAt: LocalDateTime = Arb.localDateTime().next(),
        arrivalAt: LocalDateTime = Arb.localDateTime().next(),
    ) = Flight(
        price = money,
        airline = airline,
        departure = departure,
        arrival = arrival,
        departureAt = departureAt,
        arrivalAt = arrivalAt,
    )
}
