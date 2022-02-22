package org.rsmod

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.google.inject.Injector
import dev.misfitlabs.kotlinguice4.getInstance
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import net.dodian.common.builders.GameWorldBuilder
import net.dodian.common.models.WorldStatus
import org.rsmod.game.config.GameConfig
import org.rsmod.game.config.WorldConfig
import org.rsmod.game.model.client.ClientList
import java.lang.Compiler.enable
import java.text.DateFormat

class KtorServer(private val injector: Injector) {

    fun start() {
        embeddedServer(Netty, port = 8080) {
            installJacksonContentNegotiation()

            routesWorldInfo(injector)
        }.start(wait = true)
    }
}

private fun Application.routesWorldInfo(injector: Injector) {
    val worldConfig: WorldConfig = injector.getInstance()
    val gameConfig: GameConfig = injector.getInstance()
    val clientList: ClientList = injector.getInstance()

    routing {
        get {
            call.respond(HttpStatusCode.OK, GameWorldBuilder(
                id = worldConfig.id,
                activity = worldConfig.activity,
                properties = worldConfig.flags,
                location = worldConfig.location,
                host = gameConfig.host
            ).build().apply {
                status = WorldStatus.ONLINE
                lastResponse = System.currentTimeMillis() / 1000
                population = clientList.size
            })
        }
    }
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
