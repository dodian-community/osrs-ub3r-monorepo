plugins {
    application
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

application {
    mainClass.set("org.rsmod.ServerKt")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":common"))
    implementation(project(":util"))
    implementation(project(":game"))
    implementation(project(":plugins"))
    findPlugins(project(":plugins")).forEach {
        implementation(it)
    }

    implementation("io.netty:netty-all:4.1.63.Final")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")

    implementation("io.ktor:ktor-server-core:1.6.7")
    implementation("io.ktor:ktor-server-netty:1.6.7")
    implementation("io.ktor:ktor-serialization:1.6.7")
    implementation("io.ktor:ktor-jackson:1.6.7")
}

tasks.register<JavaExec>("cache-pack") {
    mainClass.set("org.rsmod.plugins.api.cache.packer.ConfigTypePacker")
    classpath = sourceSets.main.get().runtimeClasspath
    args = emptyList()
}

tasks.register<JavaExec>("pack-items") {
    mainClass.set("org.rsmod.plugins.api.cache.packer.ConfigTypePacker")
    classpath = sourceSets.main.get().runtimeClasspath
    args = listOf("-item")
}

tasks.register<JavaExec>("pack-npcs") {
    mainClass.set("org.rsmod.plugins.api.cache.packer.ConfigTypePacker")
    classpath = sourceSets.main.get().runtimeClasspath
    args = listOf("-npc")
}

tasks.register<JavaExec>("pack-objs") {
    mainClass.set("org.rsmod.plugins.api.cache.packer.ConfigTypePacker")
    classpath = sourceSets.main.get().runtimeClasspath
    args = listOf("-objs")
}

tasks.jar {
    dependsOn(tasks.shadowJar)
}

fun findPlugins(pluginProject: ProjectDependency): List<Project> {
    val plugins = mutableListOf<Project>()
    pluginProject.dependencyProject.subprojects.forEach {
        if (it.buildFile.exists()) {
            plugins.add(it)
        }
    }
    return plugins
}
