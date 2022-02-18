package org.rsmod.plugins.api.protocol.structure.update

import io.guthix.buffer.*
import io.netty.buffer.Unpooled
import org.rsmod.game.cache.GameCache
import org.rsmod.game.event.impl.ChatMessage
import org.rsmod.plugins.api.protocol.Device
import org.rsmod.plugins.api.protocol.packet.update.*
import org.rsmod.plugins.api.protocol.structure.DevicePacketStructureMap

val structures: DevicePacketStructureMap by inject()
val cache: GameCache by inject()
val masks = structures.playerUpdate(Device.Desktop)

val huffman = cache.huffman()

/**
 *
 * 01. mask 4       - Face Pawn
 * 02. mask 32      - Direction
 * 03. mask 16      - Public Chat
 * 04. mask 64      -
 * 05. mask 1       - Appearance
 * 06. mask 1024    -
 * 07. mask 8       -
 * 08. mask 512     -
 * 09. mask 4096    - Movement Temp
 * 10. mask 256     -
 * 11. mask 2048    - Movement Perm
 * 12. mask 2       -
 *
 */
masks.order {
    -FacePawnMask::class // mask 4
    -DirectionMask::class // mask 32
    -PublicChat::class // mask 16
    -AppearanceMask::class // mask 1
    -MovementTempMask::class // mask 4096
    -MovementPermMask::class // mask 2048
}

masks.register<BitMask> {
    mask = 128
    write {
        if (packed >= 255) {
            val bitmask = packed or mask
            it.writeByte(bitmask and 255)
            it.writeByte(bitmask shr 8)
        } else {
            it.writeByte(packed and 255)
        }
    }
}

masks.register<FacePawnMask> {
    mask = 4
    write {
        it.writeShortAdd(pawnIndex)
    }
}

masks.register<DirectionMask> {
    mask = 32
    write {
        it.writeShortAdd(angle)
    }
}

masks.register<PublicChat> {
    mask = 16
    write {
        val huffmanData = ByteArray(message.text.length)
        huffman.compress(message.text, huffmanData)

        val compressed = Unpooled.compositeBuffer(2).apply {
            addComponents(
                true,
                Unpooled.buffer(2).apply { writeSmallSmart(message.text.length) },
                Unpooled.wrappedBuffer(huffmanData)
            )
        }

        it.writeShortAdd((message.color.id shl 8) or message.effect.id)
        it.writeByteNeg(message.icon)
        it.writeByte(if (message.type == ChatMessage.ChatType.AUTOCHAT) 1 else 0)
        it.writeByte(compressed.readableBytes())
        it.writeBytesAdd(compressed)
    }
}

masks.register<AppearanceMask> {
    mask = 1
    write {
        val appBuf = it.alloc().buffer()
        appBuf.writeByte(gender)
        appBuf.writeByte(skull)
        appBuf.writeByte(overheadPrayer)

        if (npc > 0) {
            appBuf.writeShort(-1)
            appBuf.writeShort(npc)
        } else {
            appBuf.writeBytes(looks)
        }

        colors.forEach { color ->
            appBuf.writeByte(color)
        }

        bas.forEach { animation ->
            appBuf.writeShort(animation)
        }

        appBuf.writeStringCP1252(username)
        appBuf.writeByte(combatLevel)
        appBuf.writeShort(0) /* unknown */
        appBuf.writeBoolean(invisible)

        it.writeByte(appBuf.writerIndex())
        it.writeBytesReversed(appBuf)
    }
}


masks.register<MovementPermMask> {
    mask = 2048
    write {
        it.writeByteNeg(type)
    }
}

masks.register<MovementTempMask> {
    mask = 4096
    write {
        it.writeByteSub(type)
    }
}
