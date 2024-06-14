import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-library`
    `java-test-fixtures`
    kotlin("jvm")
    id("io.gitlab.arturbosch.detekt")
}

val libs = the<LibrariesForLibs>()

dependencies {
    implementation(libs.bundles.kotlin.core)
    implementation(libs.bundles.kotlin.arrow)
    testImplementation(libs.bundles.testing.core)
    testFixturesImplementation(libs.bundles.testing.core)
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.jdk.get()))
    }
}

tasks {
    withType<KotlinCompile>().configureEach {
        compilerOptions {
            allWarningsAsErrors = true
            javaParameters = true
            progressiveMode = true
            freeCompilerArgs.add("-Xjsr305=strict")
        }
    }

    withType<Test> {
        useJUnitPlatform()
        systemProperty("kotest.framework.assertion.globalassertsoftly", true)
    }

    withType<Detekt> {
        this.outputs.upToDateWhen { false }
        this.outputs.doNotCacheIf("Never cache code style") { true }
        jvmTarget = libs.versions.jdk.get()
        dependencies {
            detektPlugins(libs.detekt.formatting)
        }
    }
    val detekt = tasks.named("detekt")
    val detektTasks = listOfNotNull(
        tasks.named("detektMain"),
        tasks.named("detektTest"),
        tasks.findByName("detektTestFixtures"),
        tasks.findByName("detektTestFunctional"),
    )
    tasks.named("check").configure {
        setDependsOn(dependsOn + detekt + detektTasks)
    }
    detekt.configure {
        setDependsOn(dependsOn + detektTasks)
    }
}

detekt {
    autoCorrect = true
    toolVersion = versions("detekt")
}
