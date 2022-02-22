package net.dodian.central.web.routes

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import net.dodian.central.services.WorldService

fun Application.routesGameWorlds(worldService: WorldService) {
    routing {
        get("/worlds/{id}") {
            val world = worldService.worlds.singleOrNull { it.id == call.parameters["id"]?.toInt() }

            if (world == null) {
                call.respond(HttpStatusCode.NotFound, "World by id '${call.parameters["id"]}' not found.")
            } else {
                call.respond(HttpStatusCode.OK, world)
            }
        }
        get("/worlds") {
            call.respond(HttpStatusCode.OK, worldService.worlds)
        }
    }
}
