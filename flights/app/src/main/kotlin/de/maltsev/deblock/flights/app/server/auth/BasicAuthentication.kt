package de.maltsev.deblock.flights.app.server.auth

import io.ktor.server.auth.AuthenticationConfig
import io.ktor.server.auth.UserPasswordCredential
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.basic
import io.ktor.server.routing.Route

object BasicAuthentication {

    private const val provider = "basic-auth"

    fun Route.serviceAuth(build: Route.() -> Unit) = authenticate(provider, build = build)

    data class Credentials(
        private val value: Map<String, UserPasswordCredential>,
    ) {

        constructor(value: List<UserPasswordCredential>) : this(value.associateBy { it.name })

        fun authenticate(credentials: UserPasswordCredential): Boolean = value[credentials.name]?.let {
            it.password == credentials.password
        } ?: false
    }

    fun AuthenticationConfig.basicAuthProvider(credentials: Credentials) {
        basic(provider) {
            realm = "internal"
            validate { auth ->
                val id = UserPrincipalId(auth.name)
                when {
                    credentials.authenticate(auth) -> UserPrincipal(id)
                    else -> null
                }
            }
        }
    }

    override fun toString(): String = provider
}
