package net.dodian.central.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Inject
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.http.*
import io.ktor.util.network.*
import kotlinx.coroutines.runBlocking
import net.dodian.common.builders.GameWorldBuilder
import net.dodian.common.models.GameWorld
import net.dodian.common.models.WorldLocation
import net.dodian.common.models.WorldProperty
import net.dodian.common.models.WorldStatus
import java.io.File
import java.net.ConnectException
import java.nio.file.Files
import kotlin.concurrent.thread
import kotlin.io.path.Path

private val logger = InlineLogger()

class WorldService @Inject constructor(
    private val mapper: ObjectMapper
) : Runnable {
    val worlds: MutableList<GameWorld> = mutableListOf(
        GameWorldBuilder(
            id = 999,
            activity = "Local Development",
            host = "local.dodian.net",
            properties = listOf(
                WorldProperty.BETA
            ),
            location = WorldLocation.UNITED_KINGDOM
        ).build()
    )

    private val hosts = mutableListOf<WorldHostListener>()

    private var enabled = false

    init {
        loadWorlds()
    }

    private fun loadWorlds() {
        if (Files.notExists(Path("./data/host-list.yml"))) return
        hosts.addAll(mapper.readValue(File("./data/host-list.yml")))
        logger.debug { "Checking worlds: $hosts" }
    }

    fun enable() {
        logger.info { "Enabled world service..." }
        enabled = true
        thread { this.run() }
    }

    fun disable() {
        enabled = false
    }

    override fun run() {
        while (enabled) {
            logger.debug { "Running world service update interval..." }
            hosts.forEach { host -> checkWorldHost(host) }
            Thread.sleep(3000L)
            run()
        }
    }

    private val client = HttpClient(CIO) {
        install(JsonFeature) {
            serializer = JacksonSerializer()
        }
        expectSuccess = false
    }

    private fun checkWorldHost(listenerConfig: WorldHostListener) = runBlocking {
        val world = worlds.singleOrNull { it.host == listenerConfig.host }

        try {
            val url = (if (listenerConfig.https) "https://" else "http://") +
                listenerConfig.host +
                (listenerConfig.port ?: "")

            val response: HttpResponse = client.get(url)
            if (response.status != HttpStatusCode.OK) {
                world?.status = WorldStatus.OFFLINE
                world?.population = 0
                logger.warn { "Host is unreachable: ${listenerConfig.host}" }
                return@runBlocking
            }

            val worldInfo: GameWorld = response.receive()

            if (worlds.none { it.host == listenerConfig.host }) {
                logger.debug { "Adding world: ${listenerConfig.host}" }
                worlds.add(GameWorldBuilder(
                    id = worldInfo.id,
                    host = listenerConfig.host,
                    activity = worldInfo.activity,
                    location = WorldLocation.values().firstOrNull { it.friendlyName == worldInfo.location }
                        ?: WorldLocation.UNITED_KINGDOM,
                    properties = worldInfo.flags
                ).build().apply {
                    population = worldInfo.population
                    status = worldInfo.status
                    lastResponse = System.currentTimeMillis() / 1000
                })
            } else {
                logger.debug { "Updating world: ${listenerConfig.host}" }
                world?.id = worldInfo.id
                world?.status = worldInfo.status
                world?.activity = worldInfo.activity
                world?.population = worldInfo.population
                world?.lastResponse = System.currentTimeMillis() / 1000
                world?.location = worldInfo.location
                world?.properties = worldInfo.flags.sumOf { it.id }
            }
        } catch (_: ConnectException) {
            world?.status = WorldStatus.OFFLINE
            world?.population = 0
            logger.warn { "Host is unreachable: ${listenerConfig.host}" }
        } catch (_: UnresolvedAddressException) {
            world?.status = WorldStatus.OFFLINE
            world?.population = 0
            logger.warn { "Host is unreachable: ${listenerConfig.host}" }
        }
    }
}

data class WorldHostListener(
    val host: String,
    val port: Int? = null,
    val https: Boolean
)
