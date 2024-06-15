package de.maltsev.deblock.flights.app.config

import io.ktor.server.auth.UserPasswordCredential
import kotlin.time.Duration

data class FlightsAppConfig(
    val server: ServerConfig,
    val integration: IntegrationConfig = IntegrationConfig(),
)

data class IntegrationConfig(
    val toughJet: ClientConfig? = null,
    val crazyAir: ClientConfig? = null,
)

data class ClientConfig(
    val url: String,
    val apiKey: String? = null,
    val connectionTimeout: Duration,
    val requestTimeout: Duration,
)

data class ServerConfig(
    val port: Int,
    val auth: ServerAuthConfig = ServerAuthConfig(),
)

data class ServerAuthConfig(
    val basic: BasicAuthConfig = BasicAuthConfig(),
)

data class BasicAuthConfig(
    val users: List<UserPasswordCredential> = emptyList(),
)
