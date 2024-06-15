package de.maltsev.deblock.flights.app.module

import de.maltsev.deblock.flights.app.config.IntegrationConfig
import de.maltsev.deblock.http.client.HttpClients.createHttpClient
import de.maltsev.deblock.integration.flights.crazyair.CrazyAirHttpFlightListProvider
import de.maltsev.deblock.integration.flights.toughjet.ToughJetHttpFlightListProvider

class IntegrationModule(
    integrationConfig: IntegrationConfig,
) {

    val toughJetClient = integrationConfig.toughJet?.let {
        ToughJetHttpFlightListProvider(
            client = createHttpClient(
                baseUrl = it.url,
                connectTimeout = it.connectionTimeout,
                requestTimeout = it.requestTimeout,
                authToken = it.apiKey,
            ),
        )
    }

    val crazyAirClient = integrationConfig.crazyAir?.let {
        CrazyAirHttpFlightListProvider(
            client = createHttpClient(
                baseUrl = it.url,
                connectTimeout = it.connectionTimeout,
                requestTimeout = it.requestTimeout,
                authToken = it.apiKey,
            ),
        )
    }
}
