package de.maltsev.deblock.flights.app

import de.maltsev.deblock.flights.app.config.ConfigLoader
import de.maltsev.deblock.flights.app.module.ApplicationModule
import java.io.File
import java.lang.Runtime.getRuntime

fun main(vararg args: String) {
    val configFile = args.firstOrNull()?.let(::File) ?: error("Config file is required")
    application(configFile)
        .start(wait = true)
}

fun application(configFile: File): ApplicationModule {
    val config = ConfigLoader.loadConfig(configFile)
    val app = ApplicationModule(
        config = config,
    )
    getRuntime().addShutdownHook(
        Thread {
            app.stop()
        },
    )
    return app
}
