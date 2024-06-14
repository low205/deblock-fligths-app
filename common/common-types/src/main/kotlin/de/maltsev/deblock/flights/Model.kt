package de.maltsev.deblock.flights

import java.time.LocalDateTime
import org.javamoney.moneta.Money

enum class FlightProvider(
    val displayName: String,
) {

    CRAZY_AIR("Crazy Air"),
    TOUGH_JET("Tough Jet"),
}

data class Flight(
    val price: Money,
    val airline: AirlineName,
    val departure: AirportCode,
    val arrival: AirportCode,
    val departureAt: LocalDateTime,
    val arrivalAt: LocalDateTime,
) {

    init {
        require(departure != arrival) {
            "Departure and arrival airports must be different, but were $departure"
        }
        require(!departureAt.isAfter(arrivalAt)) {
            "Departure date must be before or equal to arrival date but was $departureAt and $arrivalAt"
        }
    }
}

@JvmInline
value class AirlineName(
    val name: String,
) {

    init {
        require(name.isNotBlank()) { "Airline name must not be blank" }
    }

    override fun toString(): String = name
}

private const val AIRPORT_CODE_LENGTH = 3

@JvmInline
value class AirportCode(
    val value: String,
) {

    init {
        require(value.length == AIRPORT_CODE_LENGTH) { "Airport code must be 3 characters long" }
        require(value.all { it.isUpperCase() }) { "Airport code must be uppercase" }
        require(value.all { it.isLetter() }) { "Airport code must contain only letters" }
    }

    override fun toString(): String = value
}
