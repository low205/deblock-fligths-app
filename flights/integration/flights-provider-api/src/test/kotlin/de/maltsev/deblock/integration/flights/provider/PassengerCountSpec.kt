package de.maltsev.deblock.integration.flights.provider

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage

class PassengerCountSpec : BehaviorSpec({

    Given("Allowed passenger count") {

        listOf(1, 2, 3, 4).forAll { count ->

            When("Passenger count is $count") {
                val passengerCount = PassengerCount(count.toUInt())

                Then("Passenger count is valid") {
                    passengerCount.adults shouldBe count.toUInt()
                }

                Then("Total passengers count is valid") {
                    passengerCount.total() shouldBe count.toUInt()
                }
            }
        }
    }

    Given("Not allowed passenger count") {
        val count = 5

        When("Passengers is created") {
            val block = {
                PassengerCount(count.toUInt())
            }

            Then("Passenger count is invalid") {
                shouldThrow<IllegalArgumentException>(block) shouldHaveMessage "Maximum 4 passengers are allowed"
            }
        }
    }
})
