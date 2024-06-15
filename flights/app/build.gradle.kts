plugins {
    `common-app`
    `common-library`
    alias(libs.plugins.kotlinSerialisation)
}

dependencies {
    implementation(libs.bundles.kotlin.core)
    implementation(libs.bundles.ktor.server)

    implementation(projects.common.commonTypes)
    implementation(projects.flights.integration.crazyAir)
    implementation(projects.flights.integration.toughJet)

    testImplementation(libs.bundles.testing.core)
}

extra["mainClassName"] = "de.maltsev.deblock.flights.app.MainKt"