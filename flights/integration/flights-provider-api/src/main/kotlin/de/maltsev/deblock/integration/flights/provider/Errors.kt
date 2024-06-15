package de.maltsev.deblock.integration.flights.provider

private const val MAX_MESSAGE_LENGTH = 100

fun FlightListProvider.providerUnavailable(
    body: String,
): FlightProviderError.ProviderUnavailable = FlightProviderError.ProviderUnavailable(
    provider = provider,
    message = "${provider.displayName} provider is unavailable: ${body.take(MAX_MESSAGE_LENGTH)}",
)

fun FlightListProvider.invalidRequest(
    body: String,
): FlightProviderError.InvalidRequest = FlightProviderError.InvalidRequest(
    provider = provider,
    message = "${provider.displayName} request failed: ${body.take(MAX_MESSAGE_LENGTH)}",
)

fun FlightListProvider.requestTimeout(
    cause: Throwable? = null,
): FlightProviderError.RequestTimeout = FlightProviderError.RequestTimeout(
    provider = provider,
    message = "${provider.displayName} didn't reply in time",
    cause = cause,
)

fun FlightListProvider.otherError(
    cause: Throwable? = null,
): FlightProviderError.Other = FlightProviderError.Other(
    provider = provider,
    message = "${provider.displayName} request failed",
    cause = cause,
)
