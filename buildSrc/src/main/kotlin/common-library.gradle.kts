import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-library`
    `java-test-fixtures`
    kotlin("jvm")
    id("io.gitlab.arturbosch.detekt")
}

val libs = the<LibrariesForLibs>()

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
        testLogging {
            events = setOf(FAILED, SKIPPED)
            exceptionFormat = FULL
            showExceptions = true
            showCauses = true
            showStackTraces = true
            showStandardStreams = false
        }
        minHeapSize = "512M"
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
