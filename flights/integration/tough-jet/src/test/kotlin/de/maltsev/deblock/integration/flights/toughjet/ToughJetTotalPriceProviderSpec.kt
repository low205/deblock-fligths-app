package de.maltsev.deblock.integration.flights.toughjet

import de.maltsev.deblock.integration.flights.toughjet.ToughJetTotalPriceProvider.FlightPriceDetails
import de.maltsev.deblock.integration.flights.toughjet.ToughJetTotalPriceProvider.totalPrice
import de.maltsev.deblock.math.Percentage
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import org.javamoney.moneta.Money

class ToughJetTotalPriceProviderSpec : BehaviorSpec({

    Given("Flight price with basePrice tax and discount") {

        When("Calculating total price with positive discount") {
            val flight = FlightPriceDetails(
                basePrice = Money.of(100, "EUR"),
                tax = Money.of(5, "EUR"),
                discount = Percentage(10),
            )
            val result = flight.totalPrice()

            Then("Total price is calculated with tax after discount") {
                result shouldBe Money.of(95, "EUR")
            }
        }

        When("Calculating total price with zero discount") {
            val flight = FlightPriceDetails(
                basePrice = Money.of(100, "EUR"),
                tax = Money.of(5, "EUR"),
                discount = Percentage(0),
            )
            val result = flight.totalPrice()

            Then("Total price is calculated with tax without discount") {
                result shouldBe Money.of(105, "EUR")
            }
        }

        When("Calculating total price with negative discount") {
            val flight = {
                FlightPriceDetails(
                    basePrice = Money.of(100, "EUR"),
                    tax = Money.of(5, "EUR"),
                    discount = Percentage(-10),
                )
            }

            Then("Exception is thrown") {
                shouldThrow<IllegalArgumentException> {
                    flight()
                } shouldHaveMessage "Discount must positive but was -10.0000%"
            }
        }

        When("Calculating total price with negative discount and negative tax") {
            val flight = {
                FlightPriceDetails(
                    basePrice = Money.of(100, "EUR"),
                    tax = Money.of(-5, "EUR"),
                    discount = Percentage(-10),
                )
            }

            Then("Exception is thrown") {
                shouldThrow<IllegalArgumentException> {
                    flight()
                } shouldHaveMessage "Tax must be positive"
            }
        }

        When("Discount after applying would drop price below zero") {
            val flight = {
                FlightPriceDetails(
                    basePrice = Money.of(100, "EUR"),
                    tax = Money.of(0, "EUR"),
                    discount = Percentage(101),
                )
            }

            Then("Exception is thrown") {
                shouldThrow<IllegalArgumentException> {
                    flight()
                } shouldHaveMessage " Discount must be less than 100.0000% but was 101.0000%"
            }
        }
    }
})
