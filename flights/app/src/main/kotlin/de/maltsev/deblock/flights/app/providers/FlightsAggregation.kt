package de.maltsev.deblock.flights.app.providers

import de.maltsev.deblock.flights.Flight
import de.maltsev.deblock.flights.FlightProvider

data class FlightsAggregation(
    val flights: List<AggregatedFlight>,
)

data class AggregatedFlight(
    val provider: FlightProvider,
    val flight: Flight,
)
