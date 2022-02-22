package net.dodian.central.web.routes.cache.types

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import org.rsmod.game.cache.type.CacheType
import org.rsmod.game.cache.type.CacheTypeList

fun <T : CacheType> CacheTypeList<T>.paginated(page: Int, amount: Int = 25): List<T> {
    val start = amount * (page - 1)
    val end = start + amount
    return this.toList().subList(start, end)
}

suspend fun <T : CacheType> PipelineContext<Unit, ApplicationCall>.page(types: CacheTypeList<T>, page: Int = 1, size: Int = 25) {
    val p = call.parameters["page"]?.toInt() ?: page
    val s = call.parameters["size"]?.toInt() ?: size
    call.respond(HttpStatusCode.OK, types.paginated(p, s))
}

suspend fun <T : CacheType> PipelineContext<Unit, ApplicationCall>.typeById(types: CacheTypeList<T>, id: Int? = null) {
    val i = call.parameters["id"]?.toInt() ?: id ?: call.respond(HttpStatusCode.BadRequest, "You need to provide an ID.")
    val type = types.singleOrNull { it.id == i } ?: call.respond(HttpStatusCode.NotFound, "Type with id '$i' not found.")
    call.respond(HttpStatusCode.OK, type)
}

fun <T : CacheType> Route.routesForCacheType(parentRoute: String, types: CacheTypeList<T>) {
    route("/$parentRoute") {
        get("/") { page(types) }
        get("/page/{page}") { page(types) }
        get("/page/{page}/size/{size}") { page(types) }
        get("/{id}") { typeById(types) }
    }
}
