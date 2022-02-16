package org.rsmod.plugins.api.cache.name.enum

import com.fasterxml.jackson.databind.ObjectMapper
import org.rsmod.game.model.enum.type.EnumType
import org.rsmod.game.model.enum.type.EnumTypeList
import org.rsmod.plugins.api.cache.normalizeForNamedMap
import org.rsmod.plugins.api.cache.stripTags
import java.nio.file.Files
import java.nio.file.Path
import javax.inject.Inject

class EnumNameGenerator @Inject constructor(
    private val mapper: ObjectMapper,
    private val types: EnumTypeList
) {

    fun generate(path: Path) {
        val names = types.toNameMap()
        Files.newBufferedWriter(path).use { writer ->
            mapper.writeValue(writer, names)
        }
    }

    private fun EnumTypeList.toNameMap(): Map<String, Int> {
        val names = mutableMapOf<String, Int>()
        forEach {
            val name = it.internalName()
            names[name] = it.id
        }
        return names
    }

    private fun EnumType.internalName(): String {
        return "enum_$id"
    }
}
