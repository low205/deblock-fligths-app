package de.maltsev.deblock.http.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.endpoint
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

object HttpClients {

    fun createHttpClient(
        baseUrl: String? = null,
        connectTimeout: Duration = 1.seconds,
        requestTimeout: Duration = 2.seconds,
        maxConnectionsCount: Int = 50,
        maxConnectionsPerRoute: Int = 10,
        authToken: String? = null,
    ) = HttpClient(CIO) {
        engine {
            this.maxConnectionsCount = maxConnectionsCount
            endpoint {
                this.maxConnectionsPerRoute = maxConnectionsPerRoute
            }
        }
        install(DefaultRequest) {
            baseUrl?.let { url(it) }
            authToken?.also {
                header("Authorization", "Bearer $it")
            }
        }
        install(Logging)
        install(HttpTimeout) {
            connectTimeoutMillis = connectTimeout.inWholeMilliseconds
            requestTimeoutMillis = requestTimeout.inWholeMilliseconds
        }
        install(ContentNegotiation) {
            json()
        }
        expectSuccess = false
    }
}
