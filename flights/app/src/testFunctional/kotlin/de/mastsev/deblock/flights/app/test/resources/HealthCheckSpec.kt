package de.mastsev.deblock.flights.app.test.resources

import de.mastsev.deblock.flights.app.test.ApplicationSpec
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode.Companion.OK

class HealthCheckSpec : ApplicationSpec({

    Given("Application is running") {

        When("GET /health") {
            val response = client.get("/health")

            Then("Responds with 200 and health report") {
                response shouldHaveStatus OK
                response.body<String>() shouldEqualJson """
                    {
                        "status": "OK"
                    }
                """.trimIndent()
            }
        }
    }
})
