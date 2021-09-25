dependencies {
    implementation(libs.bcrypt)
    implementation(libs.bouncyCastle)
    implementation(libs.jacksonKotlin)
    implementation(libs.jacksonYaml)

    implementation("com.mashape.unirest:unirest-java:1.4.9")
}

tasks.create<JavaExec>("createRsa") {
    classpath = sourceSets.main.get().runtimeClasspath
    description = "Creates RSA key pair"
    main = "org.rsmod.util.security.RsaGenerator"
    args = listOf(
        "2048",
        "16",
        rootProject.projectDir.toPath().resolve("all/data/rsa/key.pem").toAbsolutePath().toString()
    )
}

tasks.create<JavaExec>("downloadPlugins") {
    group = "plugin-hub"

    classpath = sourceSets.main.get().runtimeClasspath
    description = "Creates RSA key pair"
    main = "org.rsmod.util.runelite.plugindownloader.PluginDownloader"
}

tasks.create<JavaExec>("verifyKeys") {
    group = "plugin-hub"

    classpath = sourceSets.main.get().runtimeClasspath
    description = "Creates RSA key pair"
    main = "org.rsmod.util.runelite.plugindownloader.KeyGenerator"
}
