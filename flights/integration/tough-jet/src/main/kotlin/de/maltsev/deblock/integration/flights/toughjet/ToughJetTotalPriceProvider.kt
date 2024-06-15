package de.maltsev.deblock.integration.flights.toughjet

import de.maltsev.deblock.math.Percentage
import de.maltsev.deblock.math.minus
import de.maltsev.deblock.math.plus
import org.javamoney.moneta.Money

internal object ToughJetTotalPriceProvider {

    data class FlightPriceDetails(
        val basePrice: Money,
        val tax: Money,
        val discount: Percentage,
    ) {

        init {
            require(!tax.isNegative) { "Tax must be positive" }
            require(discount >= MIN_DISCOUNT) { "Discount must positive but was $discount" }
            require(discount <= MAX_DISCOUNT) { "Discount must be less than $MAX_DISCOUNT but was $discount" }
        }
    }

    fun FlightPriceDetails.totalPrice(): Money {
        val (price, tax, discount) = this
        val totalPrice = price - discount + tax
        require(totalPrice.isPositive) { "Total price must be positive, but was $totalPrice" }
        return totalPrice
    }

    private val MIN_DISCOUNT = Percentage(value = 0)
    private val MAX_DISCOUNT = Percentage(value = 100)
}
