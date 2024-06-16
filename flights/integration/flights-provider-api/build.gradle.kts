plugins {
    `common-library`
}

dependencies {
    api(projects.common.commonTypes)
    api(libs.arrow.core)

    testImplementation(libs.bundles.testing.core)
    testImplementation(testFixtures(projects.common.commonTypes))
}