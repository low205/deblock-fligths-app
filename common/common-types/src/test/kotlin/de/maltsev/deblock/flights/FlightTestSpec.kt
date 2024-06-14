package de.maltsev.deblock.flights

import de.maltsev.deblock.fligts.test.TestData.anAirlineName
import de.maltsev.deblock.fligts.test.TestData.anAirportCode
import de.maltsev.deblock.fligts.test.TestData.anAmount
import de.maltsev.deblock.time.minus
import de.maltsev.deblock.time.plus
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import io.kotest.property.Arb
import io.kotest.property.arbitrary.localDateTime
import io.kotest.property.arbitrary.next
import kotlin.time.Duration.Companion.milliseconds

class FlightTestSpec : BehaviorSpec({

    val date = Arb.localDateTime().next()
    val laterDate = date + 1.milliseconds
    val earlierDate = date - 1.milliseconds

    Given("Flight with same departure and arrival airports") {
        val airportCode = anAirportCode()

        val block = {
            Flight(
                price = anAmount(),
                airline = anAirlineName(),
                departure = airportCode,
                arrival = airportCode,
                departureAt = earlierDate,
                arrivalAt = laterDate,
            )
        }

        Then("Exception will be thrown") {
            shouldThrow<IllegalArgumentException> {
                block()
            } shouldHaveMessage "Departure and arrival airports must be different, but were $airportCode"
        }
    }

    Given("Flight and departure date is after arrival date") {
        val departure = anAirportCode()
        val arrival = anAirportCode(except = departure)
        val block = {
            Flight(
                price = anAmount(),
                airline = anAirlineName(),
                departure = departure,
                arrival = arrival,
                departureAt = laterDate,
                arrivalAt = earlierDate,
            )
        }

        Then("Exception will be thrown") {
            shouldThrow<IllegalArgumentException>(
                block,
            ) shouldHaveMessage "Departure date must be before or equal to arrival date " +
                "but was $laterDate and $earlierDate"
        }
    }

    Given("Flight with correct parameters") {
        val departure = anAirportCode()
        val arrival = anAirportCode(except = departure)
        val price = anAmount()
        val airline = anAirlineName()
        val flight = Flight(
            price = price,
            airline = airline,
            departure = departure,
            arrival = arrival,
            departureAt = earlierDate,
            arrivalAt = laterDate,
        )

        Then("Flight is created") {
            flight.price shouldBe price
            flight.airline shouldBe airline
            flight.departure shouldBe departure
            flight.arrival shouldBe arrival
            flight.departureAt shouldBe earlierDate
            flight.arrivalAt shouldBe laterDate
        }
    }

    Given("Flight at same day") {
        val departure = anAirportCode()
        val arrival = anAirportCode(except = departure)
        val price = anAmount()
        val airline = anAirlineName()
        val flight = {
            Flight(
                price = price,
                airline = airline,
                departure = departure,
                arrival = arrival,
                departureAt = date,
                arrivalAt = date,
            )
        }

        Then("Flight is created without exception") {
            shouldNotThrowAny(flight)
        }
    }
})
