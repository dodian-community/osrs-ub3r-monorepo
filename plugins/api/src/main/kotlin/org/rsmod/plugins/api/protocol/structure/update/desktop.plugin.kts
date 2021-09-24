package org.rsmod.plugins.api.protocol.structure.update

import io.guthix.buffer.*
import org.rsmod.plugins.api.protocol.Device
import org.rsmod.plugins.api.protocol.packet.update.AppearanceMask
import org.rsmod.plugins.api.protocol.packet.update.BitMask
import org.rsmod.plugins.api.protocol.packet.update.DirectionMask
import org.rsmod.plugins.api.protocol.packet.update.MovementPermMask
import org.rsmod.plugins.api.protocol.packet.update.MovementTempMask
import org.rsmod.plugins.api.protocol.structure.DevicePacketStructureMap

val structures: DevicePacketStructureMap by inject()
val masks = structures.playerUpdate(Device.Desktop)

masks.order {
    -MovementTempMask::class
    // Exact Movement Mask
    -MovementPermMask::class
    // Animation Mask
    // Force Chat Mask
    // Face Entity Mask
    // Chat Mask
    // Graphics Mask
    -AppearanceMask::class
    -DirectionMask::class
    // Hit Mask
    // Click Ops Mask (Used for Games Room)
}

masks.register<BitMask> {
    mask = 4
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

masks.register<DirectionMask> {
    mask = 128
    write {
        it.writeShortLE(angle)
    }
}

masks.register<MovementPermMask> {
    mask = 8192
    write {
        it.writeByteSub(type)
    }
}

masks.register<MovementTempMask> {
    mask = 512
    write {
        it.writeByteAdd(type)
    }
}

masks.register<AppearanceMask> {
    mask = 2
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
        it.writeBytesAdd(appBuf)
    }
}
