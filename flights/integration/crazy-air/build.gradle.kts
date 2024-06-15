plugins {
    `common-library`
}

dependencies {
    api(projects.flights.integration.flightsProviderApi)
    implementation(libs.bundles.ktor.client)

    testImplementation(libs.bundles.testing.core)
    testImplementation(testFixtures(projects.common.commonTypes))
}