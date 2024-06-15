package de.maltsev.deblock.math

import java.math.BigDecimal
import java.math.RoundingMode.HALF_EVEN

data class Percentage(
    private val value: BigDecimal,
) {

    val isNegative: Boolean = value.signum() == -1

    fun value(): BigDecimal = value

    operator fun compareTo(other: Percentage): Int = value.compareTo(other.value)

    override fun toString(): String = "${value * percentageDivider}%"

    companion object {

        operator fun invoke(value: Int) = Percentage(value.monetary() / percentageDivider)
        operator fun invoke(value: String) = Percentage(
            value = value.monetary() / percentageDivider,
        )

        fun parse(value: String) = invoke(value)
        fun of(value: Int) = invoke(value)

        private val percentageDivider: BigDecimal = 100.monetary()
        private fun Int.monetary() = toBigDecimal().setScale(2, HALF_EVEN)
        private fun String.monetary() = toBigDecimal().setScale(2, HALF_EVEN)
    }
}
