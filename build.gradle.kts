import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jmailen.gradle.kotlinter.KotlinterExtension
import org.jmailen.gradle.kotlinter.KotlinterPlugin

plugins {
    kotlin("jvm")
    id("org.jmailen.kotlinter") apply false
}

allprojects {
    group = "org.rsmod"
    version = "0.0.1"

    apply {
        plugin("org.jetbrains.kotlin.jvm")
    }

    repositories {
        mavenCentral()
        maven("https://dl.bintray.com/michaelbull/maven")
        maven("https://jitpack.io")
        maven("https://plugins.gradle.org/m2/")
    }

    dependencies {
        implementation(kotlin("stdlib-jdk8"))
        implementation("org.jetbrains.kotlin:kotlin-reflect:1.6.10")
        implementation("com.google.inject:guice:5.1.0")
        implementation("dev.misfitlabs.kotlinguice4:kotlin-guice:1.5.0")
        implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.17.1")
        implementation("com.michael-bull.kotlin-inline-logger:kotlin-inline-logger-jvm:1.0.3")

        testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
        testImplementation("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    }

    tasks.withType<JavaCompile> {
        options.release.set(17)
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "11"
            freeCompilerArgs = listOf("-XXLanguage:+InlineClasses")
        }
    }

    plugins.withType<KotlinterPlugin> {
        configure<KotlinterExtension> {
            ignoreFailures = true
            disabledRules = arrayOf(
                "filename",
                /* https://github.com/pinterest/ktlint/issues/764 */
                "parameter-list-wrapping",
                /* https://github.com/pinterest/ktlint/issues/527 */
                "import-ordering",
                "no-wildcard-imports"
            )
        }
    }

    plugins.withType<KotlinPluginWrapper> {
        apply(plugin = "org.jmailen.kotlinter")
    }
}
