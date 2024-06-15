package de.maltsev.deblock.flights.app.module

import de.maltsev.deblock.flights.app.config.FlightsAppConfig
import de.maltsev.deblock.flights.app.resources.HealthCheckResource

class ApplicationModule(
    config: FlightsAppConfig,
) {

    val integrationModule = IntegrationModule(
        integrationConfig = config.integration,
    )

    val healthCheckResource = HealthCheckResource()

    val serverModule = ServerModule(
        serverConfig = config.server,
        resources = listOf(
            healthCheckResource,
        ),
    )

    fun start(wait: Boolean) {
        serverModule.start(wait)
    }

    fun stop() {
        serverModule.stop()
    }
}
