package net.dodian.central.web.routes

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import net.dodian.central.services.WorldService
import net.dodian.common.util.rsEncoded
import java.io.File

fun Application.routesGameClient(worldService: WorldService) {
    routing {
        get("/gamepack.jar") {
            call.respondBytes { File("./data/game-client-data/gamepack.jar").readBytes() }
        }
        get("/jav_config.ws") {
            call.respondBytes { File("./data/game-client-data/jav_config.ws").readBytes() }
        }
        get("/world_list.ws") {
            call.respondBytes { worldService.worlds.rsEncoded() }
        }
    }
}
