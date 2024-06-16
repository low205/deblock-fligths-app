package de.maltsev.deblock.flights.app.module

import de.maltsev.deblock.flights.app.config.FlightsAppConfig
import de.maltsev.deblock.flights.app.providers.FlightsAggregationProvider
import de.maltsev.deblock.flights.app.resources.health.HealthCheckResource
import de.maltsev.deblock.flights.app.resources.flights.SearchFlightsResource

class ApplicationModule(
    config: FlightsAppConfig,
) {

    val integrationModule = IntegrationModule(
        integrationConfig = config.integration,
    )

    val healthCheckResource = HealthCheckResource()

    val flightAggregationProvider = FlightsAggregationProvider(
        providers = listOfNotNull(
            integrationModule.crazyAirFlightListProvider,
            integrationModule.toughJetFlightListProvider,
        ),
    )

    val flightSearchResource = SearchFlightsResource(
        flightsProvider = flightAggregationProvider,
    )

    val serverModule = ServerModule(
        serverConfig = config.server,
        resources = listOf(
            healthCheckResource,
            flightSearchResource,
        ),
    )

    fun start(wait: Boolean) {
        serverModule.start(wait)
    }

    fun stop() {
        serverModule.stop()
    }
}
