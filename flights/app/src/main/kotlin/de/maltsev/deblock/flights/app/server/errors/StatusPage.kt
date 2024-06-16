package de.maltsev.deblock.flights.app.server.errors

import de.maltsev.deblock.exceptions.UnauthorisedException
import de.maltsev.deblock.flights.app.server.response.respondError
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import io.ktor.server.plugins.statuspages.StatusPagesConfig
import io.ktor.server.request.path
import kotlinx.serialization.json.put
import mu.KotlinLogging.logger

object StatusPage {

    private val log = logger {}

    fun configureStatusPage(config: StatusPagesConfig) = with(config) {
        exception<IllegalArgumentException> { call, cause ->
            log.warn(cause) { "Bad request: ${cause.message}" }
            call.respondError(BadRequest) {
                put("message", cause.message)
            }
        }
        exception<IllegalStateException> { call, cause ->
            log.warn(cause) { "Bad request: ${cause.message}" }
            call.respondError(BadRequest) {
                put("message", cause.message)
            }
        }
        exception<UnauthorisedException> { call, cause ->
            log.warn(cause) { "Unauthorised access to ${call.request.path()}" }
            call.respondError(Unauthorized) {
                put("message", "Unauthorised access")
            }
        }
        exception<Throwable> { call, cause ->
            log.error(cause) { "Internal server error" }
            call.respondError(InternalServerError) {
                put("message", "Internal server error")
            }
        }
        unhandled { call ->
            log.warn { "Resource not found: ${call.request.path()}" }
            call.respondError(NotFound) {
                put("message", "Resource not found")
            }
        }
    }
}
