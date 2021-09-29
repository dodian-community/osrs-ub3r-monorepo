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
    mainClass.set("org.rsmod.util.security.RsaGenerator")
    args = listOf(
        "2048",
        "16",
        rootProject.projectDir.toPath().resolve("all/data/rsa/key.pem").toAbsolutePath().toString()
    )
}

tasks.create<JavaExec>("downloadPlugins") {
    group = "plugin-hub"

    classpath = sourceSets.main.get().runtimeClasspath
    description = "Download plugins from official RuneLite plugin hub"
    mainClass.set("org.rsmod.util.runelite.PluginDownloader")
}

tasks.create<JavaExec>("verifyKeys") {
    group = "plugin-hub"

    classpath = sourceSets.main.get().runtimeClasspath
    description = "Validate public and private key for your RuneLite plugin hub"
    mainClass.set("org.rsmod.util.runelite.KeyValidator")
}
