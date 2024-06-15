package de.maltsev.deblock.integration.flights.crazyair

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import de.maltsev.deblock.flights.FlightProvider
import de.maltsev.deblock.flights.FlightProvider.CRAZY_AIR
import de.maltsev.deblock.integration.flights.crazyair.CrazyAirClientMapper.toRequest
import de.maltsev.deblock.integration.flights.crazyair.CrazyAirClientMapper.toResponse
import de.maltsev.deblock.integration.flights.provider.FlightListProvider
import de.maltsev.deblock.integration.flights.provider.FlightProviderError
import de.maltsev.deblock.integration.flights.provider.FlightsSearchRequest
import de.maltsev.deblock.integration.flights.provider.FlightsSearchResult
import de.maltsev.deblock.integration.flights.provider.invalidRequest
import de.maltsev.deblock.integration.flights.provider.otherError
import de.maltsev.deblock.integration.flights.provider.providerUnavailable
import de.maltsev.deblock.integration.flights.provider.requestTimeout
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType.Application
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.http.contentType
import kotlinx.serialization.json.JsonObject

class CrazyAirHttpFlightListProvider(
    val client: HttpClient,
) : FlightListProvider {

    override val provider: FlightProvider = CRAZY_AIR

    override suspend fun search(
        request: FlightsSearchRequest,
    ): Either<FlightProviderError, FlightsSearchResult> = runCatching {
        client.post("/search") {
            contentType(Application.Json)
            setBody(request.toRequest())
        }
    }.map {
        when {
            it.status >= InternalServerError -> providerUnavailable(it.body<String>()).left()
            it.status >= BadRequest -> invalidRequest(it.body<String>()).left()
            else -> it.body<JsonObject>().toResponse().right()
        }
    }.recover {
        when (it) {
            is HttpRequestTimeoutException,
            is ConnectTimeoutException,
            is SocketTimeoutException,
            -> requestTimeout(cause = it).left()

            else -> otherError(cause = it).left()
        }
    }.getOrThrow()
}
