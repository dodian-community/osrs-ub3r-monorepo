package net.dodian.common.util

import io.guthix.buffer.writeStringCP1252
import io.netty.buffer.Unpooled
import net.dodian.common.models.GameWorld
import net.dodian.common.models.WorldStatus

fun List<GameWorld>.rsEncoded(): ByteArray {
    val worlds = this.filter {
        it.status == WorldStatus.ONLINE
            || it.host == "127.0.0.1"
            || it.host == "localhost"
            || it.host == "local.dodian.net"
    }
    val buffer = Unpooled.buffer()
    buffer.writeInt(0) // Client doesn't use this at all.
    buffer.writeShort(worlds.size)
    worlds.forEach {
        buffer.writeShort(it.id)
        buffer.writeInt(it.properties)
        buffer.writeStringCP1252(it.host)
        buffer.writeStringCP1252(it.activity)
        buffer.writeByte(it.locationId)
        buffer.writeShort(it.population)
    }
    return buffer.array()
}
