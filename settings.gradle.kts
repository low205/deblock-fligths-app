plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "deblock-app"

include(
    "common:common-types",
    "flights:app",
    "flights:domain",
)