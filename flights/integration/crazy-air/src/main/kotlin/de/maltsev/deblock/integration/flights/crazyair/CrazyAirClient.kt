package de.maltsev.deblock.integration.flights.crazyair

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import de.maltsev.deblock.flights.FlightProvider.CRAZY_AIR
import de.maltsev.deblock.integration.flights.crazyair.CrazyAirClientMapper.toCrazyAirRequest
import de.maltsev.deblock.integration.flights.crazyair.CrazyAirClientMapper.toResponse
import de.maltsev.deblock.integration.flights.provider.FlightListProvider
import de.maltsev.deblock.integration.flights.provider.FlightProviderError
import de.maltsev.deblock.integration.flights.provider.FlightProviderError.InvalidRequest
import de.maltsev.deblock.integration.flights.provider.FlightProviderError.Other
import de.maltsev.deblock.integration.flights.provider.FlightProviderError.ProviderUnavailable
import de.maltsev.deblock.integration.flights.provider.FlightProviderError.RequestTimeout
import de.maltsev.deblock.integration.flights.provider.FlightsSearchRequest
import de.maltsev.deblock.integration.flights.provider.FlightsSearchResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.http.contentType
import io.ktor.http.path
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlinx.serialization.json.JsonObject

class CrazyAirClient(
    val client: HttpClient,
    val config: CrazyAirClientConfig,
) : FlightListProvider {

    data class CrazyAirClientConfig(
        val url: String,
        val apiKey: String,
        val connectTimeout: Duration = 1.seconds,
        val requestTimeout: Duration = 2.seconds,
    )

    override suspend fun search(
        request: FlightsSearchRequest,
    ): Either<FlightProviderError, FlightsSearchResult> = runCatching {
        client.post {
            url {
                path("/search")
            }
            bearerAuth(config.apiKey)
            contentType(ContentType.Application.Json)
            setBody(request.toCrazyAirRequest())
        }
    }.map {
        when {
            it.status >= InternalServerError -> ProviderUnavailable(
                provider = CRAZY_AIR,
                message = "${CRAZY_AIR.displayName} provider is unavailable: ${it.body<String>()}",
            ).left()

            it.status >= BadRequest -> InvalidRequest(
                provider = CRAZY_AIR,
                message = "${CRAZY_AIR.displayName} request failed: ${it.body<String>()}",
            ).left()

            else -> it.body<JsonObject>().toResponse().right()
        }
    }.recover {
        when (it) {
            is HttpRequestTimeoutException,
            is ConnectTimeoutException,
            is SocketTimeoutException,
            -> RequestTimeout(
                provider = CRAZY_AIR,
                message = "${CRAZY_AIR.displayName} didn't reply in time",
                cause = it,
            ).left()

            else -> Other(
                provider = CRAZY_AIR,
                message = "${CRAZY_AIR.displayName} request failed",
                cause = it,
            ).left()
        }
    }.getOrThrow()
}
