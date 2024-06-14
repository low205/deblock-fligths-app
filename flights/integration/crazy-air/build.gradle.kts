plugins {
    `common-kotlin-library`
}

dependencies {
    api(projects.flights.integration.flightsProviderApi)
    implementation(libs.bundles.ktor.client)

    testImplementation(testFixtures(projects.common.commonTypes))
}