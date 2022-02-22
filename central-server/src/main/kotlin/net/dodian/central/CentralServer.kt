package net.dodian.central

import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Guice
import com.google.inject.Scopes
import dev.misfitlabs.kotlinguice4.getInstance
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import net.dodian.central.config.CentralConfigModule
import net.dodian.central.config.ServerConfig
import net.dodian.central.modules.CentralCacheModule
import net.dodian.central.modules.ObjectMapperModule
import net.dodian.central.services.ServicesModule
import net.dodian.central.services.WorldService
import net.dodian.central.web.KtorServer
import org.rsmod.game.cache.GameCache
import org.rsmod.game.cache.type.CacheTypeLoaderList
import org.rsmod.game.coroutine.CoroutineModule
import org.rsmod.game.coroutine.IoCoroutineScope
import org.rsmod.plugins.api.cache.type.TypeLoaderModule

private val logger = InlineLogger()

fun main() = CentralServer().start()

class CentralServer {

    fun start() {
        logger.info { "Starting central server..." }

        val scope = Scopes.SINGLETON

        val injector = Guice.createInjector(
            CoroutineModule(scope),
            ObjectMapperModule(scope),
            CentralConfigModule(scope),
            CentralCacheModule(scope),
            TypeLoaderModule(scope),
            ServicesModule(scope)
        )

        val ioCoroutineScope: IoCoroutineScope = injector.getInstance()

        val config: ServerConfig = injector.getInstance()
        val cache: GameCache = injector.getInstance()
        cache.start()

        val typeLoaders: CacheTypeLoaderList = injector.getInstance()
        loadCacheTypes(ioCoroutineScope, typeLoaders)

        logger.debug { "Loaded server with configuration: $config" }

        val worldService: WorldService = injector.getInstance()
        worldService.enable()

        KtorServer(config, injector).start()
    }
}

private fun loadCacheTypes(
    ioCoroutineScope: IoCoroutineScope,
    loaders: CacheTypeLoaderList
) = runBlocking {
    val jobs = mutableListOf<Deferred<Unit>>()
    loaders.forEach { loader ->
        val job = ioCoroutineScope.async { loader.load() }
        jobs.add(job)
    }
    jobs.forEach { it.await() }
}
