package de.maltsev.deblock.integration.flights.provider

import de.maltsev.deblock.flights.AirportCode
import java.time.LocalDate

data class FlightsSearchRequest(
    val origin: AirportCode,
    val destination: AirportCode,
    val departureDate: LocalDate,
    val returnDate: LocalDate,
    val numberOfPassengers: PassengerCount,
) {

    init {
        require(!departureDate.isAfter(returnDate)) { "Departure date must be before return date" }
    }
}
