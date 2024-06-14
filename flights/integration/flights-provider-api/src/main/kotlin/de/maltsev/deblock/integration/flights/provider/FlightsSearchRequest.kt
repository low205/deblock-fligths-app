package de.maltsev.deblock.integration.flights.provider

import de.maltsev.deblock.flights.AirportCode
import java.time.LocalDate

data class FlightsSearchRequest(
    val origin: AirportCode,
    val destination: AirportCode,
    val departureAt: LocalDate,
    val returnAt: LocalDate,
    val passengerCount: PassengerCount,
)
