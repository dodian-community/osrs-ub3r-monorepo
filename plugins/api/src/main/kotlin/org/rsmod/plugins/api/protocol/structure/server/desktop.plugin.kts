package org.rsmod.plugins.api.protocol.structure.server

import com.github.michaelbull.logging.InlineLogger
import io.guthix.buffer.*
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import org.rsmod.game.message.PacketLength
import org.rsmod.game.model.domain.repo.XteaRepository
import org.rsmod.game.model.item.Item
import org.rsmod.game.model.map.MapSquare
import org.rsmod.plugins.api.protocol.Device
import org.rsmod.plugins.api.protocol.packet.server.*
import org.rsmod.plugins.api.protocol.structure.DevicePacketStructureMap
import org.rsmod.util.security.Xtea
import kotlin.math.min

val structures: DevicePacketStructureMap by inject()
val packets = structures.server(Device.Desktop)

packets.register<RebuildNormal> {
    opcode = 47
    length = PacketLength.Short
    write {
        val xteas = xteasBuffer(viewport, xteas)
        val buf = gpi?.write(it) ?: it
        buf.writeShortAdd(playerZone.y)
        buf.writeShortLE(playerZone.x)
        buf.writeBytes(xteas)
    }
}

packets.register<IfOpenTop> {
    opcode = 6
    length = PacketLength.Fixed
    write {
        it.writeShortAddLE(interfaceId)
    }
}

packets.register<IfOpenSub> {
    opcode = 17
    write {
        it.writeShortAddLE(interfaceId)
        it.writeIntME(targetComponent)
        it.writeByteAdd(clickMode)
    }
}

packets.register<IfMoveSub> {
    opcode = 36
    write {
        it.writeIntLE(from)
        it.writeIntLE(to)
    }
}

packets.register<PlayerInfo> {
    opcode = 38
    length = PacketLength.Short
    write {
        it.writeBytes(buffer)
    }
}

packets.register<IfSetText> {
    opcode = 53
    length = PacketLength.Short
    write {
        it.writeStringCP1252(text)
        it.writeIntIME(component)
    }
}

packets.register<VarpSmall> {
    opcode = 10
    write {
        it.writeByte(value)
        it.writeShortAdd(id)
    }
}

packets.register<VarpLarge> {
    opcode = 40
    write {
        it.writeIntLE(value)
        it.writeShort(id)
    }
}

packets.register<UpdateStat> {
    opcode = 23
    write {
        it.writeByteSub(skill)
        it.writeIntME(xp)
        it.writeByteAdd(currLevel)
    }
}

packets.register<RunClientScript> {
    opcode = 28
    length = PacketLength.Short
    write {
        val types = CharArray(args.size) { i -> if (args[i] is String) 's' else 'i' }
        it.writeStringCP1252(String(types))
        args.reversed().forEach { arg ->
            if (arg is String) it.writeStringCP1252(arg)
            else it.writeInt(arg.toString().toInt())
        }
        it.writeInt(id)
    }
}

packets.register<MessageGame> {
    opcode = 68
    length = PacketLength.Byte
    write {
        it.writeSmallSmart(type)
        it.writeBoolean(username != null)
        if (username != null) {
            it.writeStringCP1252(username)
        }
        it.writeStringCP1252(text)
    }
}

packets.register<UpdateRunEnergy> {
    opcode = 49
    write {
        it.writeByte(energy)
    }
}

packets.register<ResetClientVarCache> {
    opcode = 52
    write {}
}

packets.register<ResetAnims> {
    opcode = 76
    write {}
}

packets.register<IfSetEvents> {
    opcode = 71
    write {
        it.writeIntIME(events)
        it.writeInt(component)
        it.writeShort(dynamic.last)
        it.writeShortAdd(dynamic.first)
    }
}

//
//packets.register<NpcInfoSmallViewport> {
//    opcode = 43
//    length = PacketLength.Short
//    write {
//        it.writeBytes(buffer)
//    }
//}
//
//val logger = InlineLogger()
//
//packets.register<UpdateInvFull> {
//    opcode = 13
//    length = PacketLength.Short
//    write {
//        it.writeInt(component)
//        it.writeShort(key)
//        it.writeShort(items.size)
//        it.writeFullItemContainer(items)
//    }
//}
//
//packets.register<UpdateInvPartial> {
//    opcode = 69
//    length = PacketLength.Short
//    write {
//        it.writeInt(component)
//        it.writeShort(key)
//        it.writePartialItemContainer(updated)
//    }
//}

fun xteasBuffer(viewport: List<MapSquare>, xteasRepository: XteaRepository): ByteBuf {
    val buf = Unpooled.buffer(Short.SIZE_BYTES + (Int.SIZE_BYTES * 4 * 4))
    buf.writeShort(viewport.size)
    viewport.forEach { mapSquare ->
        val xteas = xteasRepository[mapSquare.id] ?: Xtea.EMPTY_KEY_SET
        xteas.forEach { buf.writeInt(it) }
    }
    return buf
}

fun ByteBuf.writeFullItemContainer(items: List<Item?>) {
    items.forEach { item ->
        val id = (item?.id ?: -1) + 1
        val amount = (item?.amount ?: 0)
        writeByte(min(255, amount))
        if (amount >= 255) {
            writeIntME(item?.amount ?: 0)
        }
        writeShortAddLE(id)
    }
}

fun ByteBuf.writePartialItemContainer(items: Map<Int, Item?>) {
    items.forEach { (slot, item) ->
        val id = (item?.id ?: -1) + 1
        val amount = (item?.amount ?: 0)
        writeSmallSmart(slot)
        writeShort(id)
        if (id != 0) {
            writeByte(min(255, amount))
            if (amount >= 255) {
                writeInt(item?.amount ?: 0)
            }
        }
    }
}
