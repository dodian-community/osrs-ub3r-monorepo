package org.rsmod.plugins.api.cache.name.cs2

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.michaelbull.logging.InlineLogger
import org.rsmod.game.model.cs2.Cs2Type
import org.rsmod.game.name.NamedTypeLoader
import org.rsmod.plugins.api.config.file.DefaultExtensions
import org.rsmod.plugins.api.config.file.NamedConfigFileMap
import java.nio.file.Files
import java.nio.file.Path
import javax.inject.Inject

private val logger = InlineLogger()

class Cs2NameLoader @Inject constructor(
    private val mapper: ObjectMapper,
    private val files: NamedConfigFileMap,
    private val names: Cs2NameMap,
) : NamedTypeLoader {

    override fun load(directory: Path) {
        val initialSize = names.size
        val files = files.getValue(DefaultExtensions.SCRIPT_NAMES)
        val aliasSize = files.sumOf { loadAliasFile(it) }
        logger.info { "Loaded $initialSize script names ($aliasSize ${if (aliasSize != 1) "aliases" else "alias"})" }
    }

    private fun loadAliasFile(file: Path): Int {
        var count = 0

        Files.newInputStream(file).use { input ->
            val nodes = mapper.readValue(input, LinkedHashMap<String, Int>()::class.java)
            nodes.forEach { node ->
                val key = node.key
                val value = node.value
                names[key] = Cs2Type(value)
                count++
            }
        }

        return count
    }
}
