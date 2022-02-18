package org.rsmod.plugins.api.cache.type.enum

import com.github.michaelbull.logging.InlineLogger
import io.guthix.buffer.readStringCP1252
import io.netty.buffer.ByteBuf
import org.rsmod.game.cache.GameCache
import org.rsmod.game.cache.type.CacheTypeLoader
import org.rsmod.game.model.enum.type.EnumType
import org.rsmod.game.model.enum.type.EnumTypeBuilder
import org.rsmod.game.model.enum.type.EnumTypeList
import org.rsmod.game.model.enum.type.toEnumVarType
import java.io.IOException
import javax.inject.Inject

private val logger = InlineLogger()

private const val ENUM_ARCHIVE = 2
private const val ENUM_GROUP = 8

class EnumTypeLoader @Inject constructor(
    private val cache: GameCache,
    private val types: EnumTypeList
) : CacheTypeLoader {

    override fun load() {
        val files = cache.groups(ENUM_ARCHIVE, ENUM_GROUP)
        files.forEach { (file, data) ->
            val type = data.readType(file)
            types.add(type)
        }
        logger.info { "Loaded ${types.size} enum type files" }
    }

    private fun ByteBuf.readType(id: Int): EnumType {
        val builder = EnumTypeBuilder().apply { this.id = id }
        while (isReadable) {
            val instruction = readUnsignedByte().toInt()
            if (instruction == 0) {
                break
            }
            builder.readBuffer(instruction, this)
        }
        return builder.build()
    }

    private fun EnumTypeBuilder.readBuffer(instruction: Int, buf: ByteBuf) {
        when (instruction) {
            1 -> keyType = buf.readUnsignedByte().toInt().toChar().toEnumVarType()
            2 -> valueType = buf.readUnsignedByte().toInt().toChar().toEnumVarType()
            3 -> defaultString = buf.readStringCP1252()
            4 -> defaultInt = buf.readInt()
            5 -> {
                size = buf.readUnsignedShort()
                for (i in 0 until size) {
                    values[buf.readInt()] = buf.readStringCP1252()
                }
            }
            6 -> {
                size = buf.readUnsignedShort()
                for (i in 0 until size) {
                    values[buf.readInt()] = buf.readInt()
                }
            }
            else -> throw IOException("Error unrecognised enum config code: $instruction")
        }
    }
}
