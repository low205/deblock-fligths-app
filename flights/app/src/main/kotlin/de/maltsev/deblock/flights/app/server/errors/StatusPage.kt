package de.maltsev.deblock.flights.app.server.errors

import de.maltsev.deblock.exceptions.UnauthorisedException
import de.maltsev.deblock.flights.app.server.response.respondError
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import io.ktor.server.plugins.statuspages.StatusPagesConfig
import kotlinx.serialization.json.put

object StatusPage {

    fun configureStatusPage(config: StatusPagesConfig) = with(config) {
        exception<IllegalArgumentException> { call, cause ->
            call.respondError(BadRequest) {
                put("message", cause.message)
            }
        }
        exception<IllegalStateException> { call, cause ->
            call.respondError(BadRequest) {
                put("message", cause.message)
            }
        }
        exception<UnauthorisedException> { call, _ ->
            call.respondError(Unauthorized) {
                put("message", "Unauthorised access")
            }
        }
        exception<Throwable> { call, _ ->
            call.respondError(InternalServerError) {
                put("message", "Internal server error")
            }
        }
        unhandled { call ->
            call.respondError(NotFound) {
                put("message", "Resource not found")
            }
        }
    }
}
