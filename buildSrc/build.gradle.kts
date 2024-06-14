plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
    implementation(libs.kotlin.gradle)
    implementation(libs.gradlePlugins.detekt)
    implementation(libs.gradlePlugins.shadow)
}
