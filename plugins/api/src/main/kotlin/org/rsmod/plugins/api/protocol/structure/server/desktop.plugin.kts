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
import org.rsmod.plugins.api.protocol.packet.client.ReflectionCheckReply
import org.rsmod.plugins.api.protocol.packet.server.*
import org.rsmod.plugins.api.protocol.structure.DevicePacketStructureMap
import org.rsmod.util.security.Xtea
import kotlin.math.min

val structures: DevicePacketStructureMap by inject()
val packets = structures.server(Device.Desktop)


packets.register<UpdateStat> {
    opcode = 19
    write {
        it.writeByteSub(currLevel)
        it.writeIntME(xp)
        it.writeByteSub(skill)
    }
}

packets.register<IfSetText> {
    opcode = 23
    length = PacketLength.Short
    write {
        it.writeStringCP1252(text)
        it.writeInt(component)
    }
}

packets.register<PlayerInfo> {
    opcode = 47
    length = PacketLength.Short
    write {
        it.writeBytes(buffer)
    }
}

packets.register<IfOpenSub> {
    opcode = 53
    write {
        it.writeShortAdd(interfaceId)
        it.writeInt(targetComponent)
        it.writeByte(clickMode)
    }
}

packets.register<IfOpenTop> {
    opcode = 62
    write {
        it.writeShort(interfaceId)
    }
}

packets.register<MessageGame> {
    opcode = 66
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
    opcode = 73
    write {
        it.writeByte(energy)
    }
}

packets.register<ResetClientVarCache> {
    opcode = 90
    write {}
}

packets.register<ResetAnims> {
    opcode = 64
    write {}
}

packets.register<NpcInfoSmallViewport> {
    opcode = 43
    length = PacketLength.Short
    write {
        it.writeBytes(buffer)
    }
}

packets.register<VarpSmall> {
    opcode = 44
    write {
        it.writeShort(id)
        it.writeByteAdd(value)
    }
}

packets.register<VarpLarge> {
    opcode = 31
    write {
        it.writeShortLE(id)
        it.writeIntLE(value)
    }
}
val logger = InlineLogger()
packets.register<RunClientScript> {
    opcode = 92
    length = PacketLength.Short
    write {
        val types = CharArray(args.size) { i -> if (args[i] is String) 's' else 'i' }
        it.writeStringCP1252(String(types))
        args.reversed().forEach { arg ->
            if (arg is String) it.writeStringCP1252(arg)
            else it.writeInt(arg.toString().toInt())
        }
        it.writeInt(id)

        logger.debug { "Writing RunClientScript (id=$id, types=${String(types)}, args=$args)" }
    }
}

packets.register<RebuildNormal> {
    opcode = 94
    length = PacketLength.Short
    write {
        val xteas = xteasBuffer(viewport, xteas)
        val buf = gpi?.write(it) ?: it
        buf.writeShort(playerZone.y)
        buf.writeShort(playerZone.x)
        buf.writeBytes(xteas)
    }
}

packets.register<UpdateInvFull> {
    opcode = 13
    length = PacketLength.Short
    write {
        it.writeInt(component)
        it.writeShort(key)
        it.writeShort(items.size)
        it.writeFullItemContainer(items)
    }
}

packets.register<UpdateInvPartial> {
    opcode = 69
    length = PacketLength.Short
    write {
        it.writeInt(component)
        it.writeShort(key)
        it.writePartialItemContainer(updated)
    }
}

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
