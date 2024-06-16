package de.maltsev.deblock.flights

import org.javamoney.moneta.Money
import java.time.LocalDateTime

enum class FlightProvider(
    val displayName: String,
) {

    CRAZY_AIR("CrazyAir"),
    TOUGH_JET("ToughJet"),
}

data class Flight(
    val price: Money,
    val airline: AirlineName,
    val departureFrom: AirportCode,
    val arrivalTo: AirportCode,
    val departureAt: LocalDateTime,
    val arrivalAt: LocalDateTime,
) {

    init {
        require(departureFrom != arrivalTo) {
            "Departure and arrival airports must be different, but were $departureFrom"
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

enum class CabinClass {
    ECONOMY,
    BUSINESS,
}
