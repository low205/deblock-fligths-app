package de.maltsev.deblock.flights.app.resources.health

import de.maltsev.deblock.flights.app.resources.Resource
import de.maltsev.deblock.flights.app.resources.Resources.HealthCheck
import de.maltsev.deblock.flights.app.server.response.respondJson
import io.ktor.server.application.call
import io.ktor.server.resources.get
import io.ktor.server.routing.Routing
import kotlinx.serialization.json.put

class HealthCheckResource : Resource {

    override fun Routing.register() {
        get<HealthCheck> {
            call.respondJson {
                put("status", "OK")
            }
        }
    }
}
