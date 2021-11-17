package org.rsmod.plugins.api.protocol.structure.login

import io.guthix.buffer.readIntIME
import io.guthix.buffer.readIntME
import org.rsmod.game.cache.GameCache
import org.rsmod.plugins.api.protocol.packet.login.AuthCode
import org.rsmod.plugins.api.protocol.packet.login.CacheChecksum
import org.rsmod.plugins.api.protocol.packet.login.LoginPacketMap

val packets: LoginPacketMap by inject()
val cache: GameCache by inject()

packets.register {
    val code = when (readByte().toInt()) {
        2 -> {
            skipBytes(Int.SIZE_BYTES)
            -1
        }
        1, 3 -> {
            val auth = readUnsignedMedium()
            skipBytes(Byte.SIZE_BYTES)
            auth
        }
        else -> readInt()
    }
    AuthCode(code)
}

packets.register {
    val crcs = IntArray(cache.archiveCount)
    crcs[10] = readIntIME()
    crcs[6] = readInt()
    crcs[16] = readInt()
    crcs[20] = readIntIME()
    crcs[2] = readIntLE()
    crcs[0] = readIntME()
    crcs[3] = readIntME()
    crcs[15] = readIntIME()
    crcs[17] = readIntIME()
    crcs[9] = readIntIME()
    crcs[7] = readInt()
    crcs[4] = readIntLE()
    crcs[5] = readIntIME()
    crcs[8] = readIntME()
    crcs[12] = readIntLE()
    crcs[18] = readIntLE()
    crcs[13] = readIntLE()
    crcs[1] = readInt()
    crcs[19] = readIntIME()
    crcs[11] = readIntIME()
    crcs[14] = readIntLE()
    CacheChecksum(crcs)
}
