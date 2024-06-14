package de.maltsev.deblock.time

import java.time.LocalDateTime
import kotlin.time.Duration
import kotlin.time.toJavaDuration

operator fun LocalDateTime.plus(
    duration: Duration,
): LocalDateTime = plus(duration.toJavaDuration())

operator fun LocalDateTime.minus(
    duration: Duration,
): LocalDateTime = minus(duration.toJavaDuration())
