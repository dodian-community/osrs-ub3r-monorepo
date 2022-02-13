val rootPluginDir = projectDir
val rootPluginBuildDir = buildDir

val appDir = project(":all").projectDir
val pluginConfigDir = appDir.resolve("plugins").resolve("resources")

subprojects {
    val relative = projectDir.relativeTo(rootPluginDir)
    buildDir = rootPluginBuildDir.resolve(relative)
    group = "org.rsmod.plugins"

    dependencies {
        implementation(kotlin("stdlib"))
        implementation(project(":game"))
        implementation("org.jetbrains.kotlin:kotlin-script-runtime:1.6.10")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
        implementation("org.rsmod:pathfinder:1.2.4")
    }

    tasks.register("install") {
        copyResources(project)
    }
}

tasks.register("install-plugins") {
    subprojects.forEach { project ->
        copyResources(project)
    }
}

tasks.register("install-plugins-fresh") {
    file(pluginConfigDir).deleteRecursively()
    subprojects.forEach { project ->
        copyResources(project)
    }
}

fun copyResources(project: Project) {
    val relativePluginDir = project.projectDir.relativeTo(rootPluginDir)
    val pluginResourceFiles = project.sourceSets.main.get().resources.asFileTree
    if (pluginResourceFiles.isEmpty) return
    val configDirectory = pluginConfigDir.resolve(relativePluginDir)
    pluginResourceFiles.forEach { file ->
        val existingFile = configDirectory.resolve(file.name)
        if (existingFile.exists()) {
            /* do not overwrite existing config files */
            return@forEach
        }
        copy {
            from(file)
            into(configDirectory)
        }
    }
}
