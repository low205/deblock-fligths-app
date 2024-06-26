package de.maltsev.deblock.integration.flights.provider

import de.maltsev.deblock.flights.FlightProvider

sealed class FlightProviderError {

    abstract val provider: FlightProvider
    abstract val cause: Throwable?

    data class InvalidRequest(
        override val provider: FlightProvider,
        val message: String,
        override val cause: Throwable? = null,
    ) : FlightProviderError()

    data class ProviderUnavailable(
        override val provider: FlightProvider,
        val message: String,
        override val cause: Throwable? = null,
    ) : FlightProviderError()

    data class RequestTimeout(
        override val provider: FlightProvider,
        val message: String,
        override val cause: Throwable? = null,
    ) : FlightProviderError()

    data class Other(
        override val provider: FlightProvider,
        val message: String,
        override val cause: Throwable? = null,
    ) : FlightProviderError()
}
