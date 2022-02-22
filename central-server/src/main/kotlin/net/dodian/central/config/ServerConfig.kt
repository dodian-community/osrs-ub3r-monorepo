package net.dodian.central.config

import java.nio.file.Path

data class ServerConfig(
    val port: Int,
    val host: String,
    val cachePath: Path
)
