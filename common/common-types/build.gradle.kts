plugins {
    `common-library`
}

dependencies {
    api(libs.core.money) {
        exclude(group = "com.squareup.okhttp3", module = "okhttp")
    }
    api(libs.kotlin.serialisation.json)
    api(libs.kotlin.serialisation.yaml)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.serialization.kotlinx.json)

    testImplementation(libs.bundles.testing.core)

    testFixturesImplementation(libs.testing.kotest.property)
    testFixturesImplementation(libs.testing.kotest.property.arbs)
}