package de.maltsev.deblock.exceptions

open class UnauthorisedException(
    override val message: String,
    override val cause: Throwable? = null,
) : DeblockException()
