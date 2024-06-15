package de.maltsev.deblock.flights.app.server

import de.maltsev.deblock.flights.app.config.BasicAuthConfig
import de.maltsev.deblock.flights.app.resources.Resource
import de.maltsev.deblock.flights.app.server.auth.BasicAuthentication.Credentials
import de.maltsev.deblock.flights.app.server.auth.BasicAuthentication.basicAuthProvider
import de.maltsev.deblock.flights.app.server.auth.UserPrincipal
import de.maltsev.deblock.flights.app.server.errors.StatusPage
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.principal
import io.ktor.server.plugins.callid.CallId
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.resources.Resources
import io.ktor.server.routing.routing

fun Application.module(
    resources: List<Resource>,
    basicAuthCredentials: BasicAuthConfig,
) {
    install(DefaultHeaders)
    install(CallId)
    install(CallLogging) {
        mdc("USER_ID") { call ->
            call.principal<UserPrincipal>()?.id?.value
        }
    }
    install(Authentication) {
        basicAuthProvider(Credentials(basicAuthCredentials.users))
    }
    install(ContentNegotiation) {
        json()
    }
    install(Resources)
    install(StatusPages, StatusPage::configureStatusPage)
    routing {
        resources.forEach { resource ->
            resource.apply { register() }
        }
    }
}
