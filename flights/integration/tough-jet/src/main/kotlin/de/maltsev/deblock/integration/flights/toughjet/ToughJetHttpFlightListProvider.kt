package de.maltsev.deblock.integration.flights.toughjet

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import de.maltsev.deblock.flights.FlightProvider
import de.maltsev.deblock.flights.FlightProvider.TOUGH_JET
import de.maltsev.deblock.integration.flights.provider.FlightListProvider
import de.maltsev.deblock.integration.flights.provider.FlightProviderError
import de.maltsev.deblock.integration.flights.provider.FlightsSearchRequest
import de.maltsev.deblock.integration.flights.provider.FlightsSearchResult
import de.maltsev.deblock.integration.flights.provider.invalidRequest
import de.maltsev.deblock.integration.flights.provider.otherError
import de.maltsev.deblock.integration.flights.provider.providerUnavailable
import de.maltsev.deblock.integration.flights.provider.requestTimeout
import de.maltsev.deblock.integration.flights.toughjet.ToughJetClientMapper.toRequest
import de.maltsev.deblock.integration.flights.toughjet.ToughJetClientMapper.toResponse
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

class ToughJetHttpFlightListProvider(
    private val client: HttpClient,
) : FlightListProvider {

    override val provider: FlightProvider = TOUGH_JET

    override suspend fun search(
        request: FlightsSearchRequest,
    ): Either<FlightProviderError, FlightsSearchResult> = runCatching {
        client.post("/flights") {
            contentType(Application.Json)
            setBody(request.toRequest())
        }
    }.map {
        when {
            it.status >= InternalServerError -> providerUnavailable("${it.status} ${it.body<String>()}").left()
            it.status >= BadRequest -> invalidRequest("${it.status} ${it.body<String>()}").left()
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
