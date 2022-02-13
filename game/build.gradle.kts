dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":util"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
    implementation("io.github.classgraph:classgraph:4.8.138")
    implementation("org.jetbrains.kotlin:kotlin-scripting-common:1.6.10")
    implementation("org.jetbrains.kotlin:kotlin-script-runtime:1.6.10")
    implementation("org.bouncycastle:bcprov-jdk15on:1.70")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.1")
    implementation("io.guthix:jagex-store-5:0.4.0")
    implementation("io.netty:netty-all:4.1.63.Final")
}
