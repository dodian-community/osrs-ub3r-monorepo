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
    crcs[10] = readIntME()
    crcs[0] = readIntLE()
    crcs[3] = readIntLE()
    crcs[4] = readIntME()
    crcs[16] = readIntLE()
    crcs[8] = readInt()
    crcs[14] = readInt()
    crcs[15] = readInt()
    crcs[6] = readIntIME()
    crcs[2] = readIntLE()
    crcs[5] = readIntME()
    crcs[20] = readIntIME()
    crcs[13] = readIntIME()
    crcs[1] = readIntIME()
    crcs[11] = readIntLE()
    crcs[17] = readIntIME()
    crcs[7] = readIntME()
    crcs[18] = readIntLE()
    crcs[9] = readIntLE()
    crcs[19] = readIntLE()
    crcs[12] = readInt()
    CacheChecksum(crcs)
}
