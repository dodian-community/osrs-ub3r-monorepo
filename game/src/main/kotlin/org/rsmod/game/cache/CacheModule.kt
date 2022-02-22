package org.rsmod.game.cache

import com.google.inject.Provider
import com.google.inject.Scope
import dev.misfitlabs.kotlinguice4.KotlinModule
import io.guthix.js5.Js5Cache
import io.guthix.js5.container.disk.Js5DiskStore
import net.dodian.common.zip.unzip
import org.rsmod.game.config.GameConfig
import java.io.BufferedInputStream
import java.io.FileOutputStream
import java.net.URL
import java.nio.file.Files
import javax.inject.Inject
import kotlin.io.path.exists
import kotlin.io.path.notExists

class CacheModule(private val scope: Scope) : KotlinModule() {

    override fun configure() {
        bind<GameCache>()
            .toProvider<GameCacheProvider>()
            .`in`(scope)
    }
}

private class GameCacheProvider @Inject constructor(
    private val gameConfig: GameConfig
) : Provider<GameCache> {

    private val path = gameConfig.cachePath.resolve(PACKED_FOLDER)

    private fun downloadCacheZip() {
        BufferedInputStream(URL("${gameConfig.centralServer}cache.zip").openStream()).use { input ->
            FileOutputStream(path.resolve("cache.zip").toString()).use { output ->
                val dataBuffer = ByteArray(1024)
                var bytesRead: Int
                while (input.read(dataBuffer, 0, 1024).also { bytesRead = it } != -1) {
                    output.write(dataBuffer, 0, bytesRead)
                }
                output.close()
            }
            input.close()
        }
    }

    private fun unzipCache() {
        path.resolve("cache.zip").unzip(path, deleteAfter = true)
    }

    private fun isCacheInstalled()
        = Files.exists(path) && Files.exists(path.resolve("main_file_cache.dat2"))

    override fun get(): GameCache {
        if (!Files.isDirectory(path)) {
            path.toFile().mkdirs()
        }

        if (!isCacheInstalled()) {
            downloadCacheZip()
            unzipCache()
        }

        if (path.resolve("main_file_cache.dat2").notExists())
            error("Cache isn't present at: ${path.toAbsolutePath()}")

        val diskStore = Js5DiskStore.open(path)
        val cache = Js5Cache(diskStore)
        return GameCache(path, diskStore, cache)
    }

    companion object {
        private const val PACKED_FOLDER = "packed"
    }
}
