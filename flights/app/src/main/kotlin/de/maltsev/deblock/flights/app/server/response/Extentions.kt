package de.maltsev.deblock.flights.app.server.response

import io.ktor.http.HttpStatusCode
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.putJsonObject

suspend fun ApplicationCall.respondJson(
    status: HttpStatusCode = OK,
    builder: JsonObjectBuilder.() -> Unit,
) = respond(status, buildJsonObject(builder))

suspend fun ApplicationCall.respondError(
    status: HttpStatusCode,
    builder: JsonObjectBuilder.() -> Unit,
) = respondJson(status) {
    putJsonObject("error", builder)
}
