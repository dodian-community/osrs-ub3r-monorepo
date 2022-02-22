package net.dodian.central.web.routes

import com.google.inject.Injector
import dev.misfitlabs.kotlinguice4.getInstance
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import net.dodian.central.web.routes.cache.types.routesForCacheType
import net.dodian.common.cache.CacheZip
import org.rsmod.game.model.item.type.ItemTypeList
import org.rsmod.game.model.npc.type.NpcTypeList
import org.rsmod.game.model.obj.type.ObjectTypeList

fun Application.routesGameCache(injector: Injector) {
    routing {
        route("/cache") {
            routesForCacheType("items", injector.getInstance<ItemTypeList>())
            routesForCacheType("npcs", injector.getInstance<NpcTypeList>())
            routesForCacheType("objects", injector.getInstance<ObjectTypeList>())
        }
        get("/cache.zip") {
            val cacheZip = CacheZip()
            cacheZip.createTempZip()
            call.respondBytes { cacheZip.bytes() }
        }
    }
}
