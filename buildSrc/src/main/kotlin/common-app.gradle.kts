import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.kotlin.dsl.invoke

plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
    `jvm-test-suite`
}

testing {
    suites {
        val test by getting(JvmTestSuite::class)
        register<JvmTestSuite>("testFunctional") {
            dependencies {
                implementation(project())
            }
            targets {
                all {
                    testTask.configure {
                        shouldRunAfter(test)
                    }
                }
            }
        }
    }
}

val testFunctionalImplementation: Configuration by configurations
configurations {
    testFunctionalImplementation.extendsFrom(testImplementation.get())
}

extra["shadowJarVersion"] = project.buildShadowJarVersion

val mainClassName: String by extra

tasks {
    check {
        dependsOn(testing.suites.named("testFunctional"))
    }
    "build" {
        dependsOn("shadowJar")
    }
    named<ShadowJar>("shadowJar") {
        isZip64 = true
        mergeServiceFiles()
        manifest {
            attributes(
                mapOf(
                    "Main-Class" to mainClassName,
                    "Application-Name" to "$applicationName",
                    "Build-Time" to buildTime,
                    "Build-Number" to buildNumber,
                    "Build-Version" to buildShadowJarVersion,
                    "Build-Environment" to if (isCi) "CI" else "Local"
                )
            )
        }
        archiveVersion.set(project.buildShadowJarVersion)
        archiveFileName.set("${applicationName}-${buildShadowJarVersion}-all.jar")
    }
}
