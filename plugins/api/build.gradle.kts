import java.nio.file.Path

version = "194.0.0"

dependencies {
    implementation(project(":util"))
    implementation("io.netty:netty-all:4.1.63.Final")
    implementation("com.michael-bull.kotlin-retry:kotlin-retry:1.0.9")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.1")
    implementation("io.guthix:jagex-bytebuf:0.1.4")
    implementation("io.guthix:jagex-store-5:0.4.0")
}

tasks.create<JavaExec>("generateNames") {
    group = "dodian-setup"
    classpath = sourceSets.main.get().runtimeClasspath
    workingDir = project(":game-server").projectDir
    mainClass.set("org.rsmod.plugins.api.cache.name.NameGenerator")
}
