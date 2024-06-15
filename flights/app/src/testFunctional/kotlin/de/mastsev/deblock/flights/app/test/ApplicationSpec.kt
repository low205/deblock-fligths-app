package de.mastsev.deblock.flights.app.test

import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.BehaviorSpec
import mu.KotlinLogging.logger
import java.util.concurrent.ConcurrentHashMap

abstract class ApplicationSpec(spec: ApplicationSpec.() -> Unit = {}) : BehaviorSpec() {

    val log = logger {}
    val app = get("default")
    val client = app.client

    override suspend fun beforeSpec(spec: Spec) {
        log.info { "Starting test ${javaClass.canonicalName}" }
        app.awaitStart()
    }

    init {
        runCatching {
            spec()
        }.onFailure {
            log.error(it) { "Initialisation of test ${javaClass.canonicalName} failed" }
        }
    }

    companion object {

        private val apps = ConcurrentHashMap<String, TestFlightsApp>()
        fun get(name: String) = apps.computeIfAbsent(name) { TestFlightsApp() }
    }
}
