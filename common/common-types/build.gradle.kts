plugins {
    `common-kotlin-library`
}

dependencies {
    api(libs.core.money)
    api(libs.kotlin.serialisation.json)

    implementation(libs.bundles.ktor.client)
}