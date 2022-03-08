dependencies {
    implementation("org.mindrot:jbcrypt:0.4")
    implementation("org.bouncycastle:bcprov-jdk15on:1.70")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.1")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.13.1")

    implementation("com.mashape.unirest:unirest-java:1.4.9")
}

tasks.create<JavaExec>("createRsa") {
    group = "dodian-setup"
    classpath = sourceSets.main.get().runtimeClasspath
    description = "Creates RSA key pair"
    mainClass.set("org.rsmod.util.security.RsaGenerator")
    args = listOf(
        "2048",
        "16",
        rootProject.projectDir.toPath().resolve("game-server/data/rsa/key.pem").toAbsolutePath().toString()
    )
}

tasks.create<JavaExec>("downloadPlugins") {
    group = "runelite-plugin-hub"

    classpath = sourceSets.main.get().runtimeClasspath
    description = "Download plugins from official RuneLite plugin hub"
    mainClass.set("org.rsmod.util.runelite.PluginDownloader")
}

tasks.create<JavaExec>("verifyKeys") {
    group = "runelite-plugin-hub"

    classpath = sourceSets.main.get().runtimeClasspath
    description = "Validate public and private key for your RuneLite plugin hub"
    mainClass.set("org.rsmod.util.runelite.KeyValidator")
}
