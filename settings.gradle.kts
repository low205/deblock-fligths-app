plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "deblock-app"

include(
    "common:common-types",
    "flights:app",
    "flights:domain",
    "flights:integration:crazy-air",
    "flights:integration:flights-provider-api",
    "flights:integration:tough-jet",
)

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")