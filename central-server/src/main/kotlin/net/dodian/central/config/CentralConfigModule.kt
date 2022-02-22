package net.dodian.central.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.inject.Provider
import javax.inject.Inject
import com.google.inject.Scope
import dev.misfitlabs.kotlinguice4.KotlinModule
import java.nio.file.Path

class CentralConfigModule(
    private val scope: Scope
) : KotlinModule() {

    override fun configure() {
        bind<ServerConfig>()
            .toProvider<ServerConfigProvider>()
            .`in`(scope)
    }
}

class ServerConfigProvider @Inject constructor(
    private val mapper: ObjectMapper
) : Provider<ServerConfig> {

    override fun get(): ServerConfig {
        return mapper.readValue(CONFIG_PATH.toFile())
    }

    companion object {
        private val CONFIG_PATH = Path.of(".", "config.yml")
    }
}
