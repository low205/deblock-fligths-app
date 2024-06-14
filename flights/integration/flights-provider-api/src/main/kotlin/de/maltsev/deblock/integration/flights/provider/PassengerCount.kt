package de.maltsev.deblock.integration.flights.provider

data class PassengerCount(
    val adults: UInt,
) {

    fun total() = adults
}
