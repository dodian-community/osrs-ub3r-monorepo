package net.dodian.central.web

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.google.inject.Injector
import dev.misfitlabs.kotlinguice4.getInstance
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.jackson.*
import io.ktor.response.*
import io.ktor.routing.*
import net.dodian.central.config.ServerConfig
import net.dodian.central.services.WorldService
import net.dodian.central.web.routes.routesGameCache
import net.dodian.central.web.routes.routesGameClient
import net.dodian.central.web.routes.routesGameWorlds
import java.text.DateFormat

class KtorServer(
    private val config: ServerConfig,
    private val injector: Injector
) {

    private val worldService: WorldService = injector.getInstance()

    fun start() {
        embeddedServer(Netty, port = config.port, host = config.host) {
            installJacksonContentNegotiation()

            routesGameWorlds(worldService)
            routesGameClient(worldService)
            routesGameCache(injector)

            routing {
                get("/fittetryne") {
                    call.respondText { "hello" }
                }
            }
        }.start(wait = true)
    }

    private fun Application.installJacksonContentNegotiation() {
        install(ContentNegotiation) {
            jackson {
                findAndRegisterModules()
                registerKotlinModule()

                enable(SerializationFeature.INDENT_OUTPUT)

                dateFormat = DateFormat.getDateInstance()

                visibilityChecker.withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                visibilityChecker.withFieldVisibility(JsonAutoDetect.Visibility.ANY)
            }
        }
    }
}
