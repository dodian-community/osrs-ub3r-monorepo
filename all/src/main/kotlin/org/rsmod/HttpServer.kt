package org.rsmod

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.io.File

fun main() {

    embeddedServer(Netty, port = 80) {
        routing {
            get("/") {
                call.respondFile(File("./all/data/gamepack.jar"))
            }
            get("/gamepack.jar") {
                call.respondFile(File("./all/data/gamepack.jar"))
            }
        }
    }.start(wait = true)
}
