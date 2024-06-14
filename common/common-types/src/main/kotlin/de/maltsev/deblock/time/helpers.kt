package de.maltsev.deblock.time

import java.time.LocalDate
import kotlin.time.Duration

operator fun LocalDate.plus(
    duration: Duration,
): LocalDate = plusDays(duration.inWholeDays)

operator fun LocalDate.minus(
    duration: Duration,
): LocalDate = minusDays(duration.inWholeDays)