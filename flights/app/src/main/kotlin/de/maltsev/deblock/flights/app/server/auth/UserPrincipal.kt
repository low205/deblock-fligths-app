package de.maltsev.deblock.flights.app.server.auth

import io.ktor.server.auth.Principal

data class UserPrincipal(
    val id: UserPrincipalId,
) : Principal

@JvmInline
value class UserPrincipalId(
    val value: String,
)
