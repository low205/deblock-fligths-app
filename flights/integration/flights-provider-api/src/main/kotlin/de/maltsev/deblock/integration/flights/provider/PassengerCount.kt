package de.maltsev.deblock.integration.flights.provider

data class PassengerCount(
    val adults: UInt,
) {

    init {
        require(adults <= 4u) {
            "Maximum 4 passengers are allowed"
        }
    }

    fun total() = adults
}
