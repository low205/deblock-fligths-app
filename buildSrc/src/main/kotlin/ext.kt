import java.time.LocalDateTime
import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.getByType

val isCi = System.getenv("CI").toBoolean()

val Project.buildShadowJarVersion: String
    get() = properties["buildShadowJarVersion"]?.toString() ?: project.version.toString()
val Project.buildNumber: String
    get() = properties["buildNumber"]?.toString() ?: "0"
val Project.applicationName: String
    get() = project.name
val Project.buildTime: String
    get() = properties["buildTime"]?.toString() ?: LocalDateTime.now().toString()

fun Project.libs(name: String): Provider<MinimalExternalModuleDependency> = extensions
    .getByType<VersionCatalogsExtension>()
    .named("libs")
    .findLibrary(name)
    .get()

fun Project.versions(name: String) = extensions
    .getByType<VersionCatalogsExtension>()
    .named("libs")
    .findVersion(name)
    .get()
    .requiredVersion

fun Project.bundles(name: String) = extensions
    .getByType<VersionCatalogsExtension>()
    .named("libs")
    .findBundle(name)
    .get()
