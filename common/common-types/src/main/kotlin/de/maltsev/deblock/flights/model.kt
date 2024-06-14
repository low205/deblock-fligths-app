package de.maltsev.deblock.flights

import java.time.LocalDate
import org.javamoney.moneta.Money

data class Flight(
    val price: Money,
    val airline: AirlineName,
    val departure: AirportCode,
    val arrival: AirportCode,
    val departureAt: LocalDate,
    val arrivalAt: LocalDate,
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

@JvmInline
value class AirportCode(
    val value: String,
) {

    init {
        require(value.length == 3) { "Airport code must be 3 characters long" }
        require(value.all { it.isUpperCase() }) { "Airport code must be uppercase" }
        require(value.all { it.isLetter() }) { "Airport code must contain only letters" }
    }

    override fun toString(): String = value
}