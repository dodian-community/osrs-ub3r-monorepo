package org.rsmod.plugins.api.protocol.structure.client

import com.github.michaelbull.logging.InlineLogger
import io.guthix.buffer.*
import org.rsmod.game.message.ClientPacket
import org.rsmod.plugins.api.protocol.Device
import org.rsmod.plugins.api.protocol.packet.client.*
import org.rsmod.plugins.api.protocol.structure.DevicePacketStructureMap

val logger = InlineLogger()

val structures: DevicePacketStructureMap by inject()
val packets = structures.client(Device.Desktop)

packets.register<MoveGameClick> {
    opcode = 57
    length = -1
    handler = GameClickHandler::class
    read {
        val type = readUnsignedByteAdd().toInt()
        val x = readUnsignedShortLE()
        val y = readUnsignedShortAddLE()
        MoveGameClick(x, y, type)
    }
}

packets.register<MoveMinimapClick> {
    opcode = 66
    length = -1
    handler = MinimapClickHandler::class
    read {
        val type = readByteAdd().toInt()
        val x = readUnsignedShortLE()
        val y = readUnsignedShortAddLE()
        MoveMinimapClick(x, y, type)
    }
}

packets.register<ClientPacket> {
    opcode = 0
    length = -1
}

packets.register<ClientPacket> {
    opcode = 1
    length = -2
}

packets.register<ClientPacket> {
    opcode = 2
    length = 8
}

packets.register<ClientPacket> {
    opcode = 3
    length = 15
}

packets.register<ClientPacket> {
    opcode = 4
    length = 16
}

packets.register<ClientPacket> {
    opcode = 5
    length = -1
}

packets.register<ClientPacket> {
    opcode = 6
    length = 3
}

packets.register<ClientPacket> {
    opcode = 7
    length = 4
}

packets.register<ClientPacket> {
    opcode = 8
    length = 3
}

packets.register<ClientPacket> {
    opcode = 9
    length = 7
}

packets.register<ClientPacket> {
    opcode = 10
    length = 8
}

packets.register<ClientPacket> {
    opcode = 11
    length = 3
}

packets.register<ClientPacket> {
    opcode = 12
    length = 16
}

packets.register<ClientPacket> {
    opcode = 13
    length = 8
}
packets.register<ClientPacket> {
    opcode = 14
    length = -1
}
packets.register<ClientPacket> {
    opcode = 15
    length = 16
}
packets.register<ClientPacket> {
    opcode = 16
    length = 2
}
packets.register<EventMouseClick> {
    opcode = 17
    length = 6
}
packets.register<ClientPacket> {
    opcode = 18
    length = 3
}
packets.register<ClientPacket> {
    opcode = 19
    length = 7
}
packets.register<ClientPacket> {
    opcode = 20
    length = 15
}
packets.register<ClientPacket> {
    opcode = 21
    length = -1
}
packets.register<ClientPacket> {
    opcode = 22
    length = 4
}
packets.register<ClientPacket> {
    opcode = 23
    length = 8
}
packets.register<WindowStatus> {
    opcode = 24
    length = 5
}
packets.register<ClientPacket> {
    opcode = 25
    length = 4
}
packets.register<ClientPacket> {
    opcode = 26
    length = -1
}
packets.register<ClientPacket> {
    opcode = 27
    length = -1
}
packets.register<ClientPacket> {
    opcode = 28
    length = 8
}
packets.register<ClientPacket> {
    opcode = 29
    length = 7
}
packets.register<ClientPacket> {
    opcode = 30
    length = 3
}
packets.register<ClientPacket> {
    opcode = 31
    length = 8
}
packets.register<ClientPacket> {
    opcode = 32
    length = -1
}
packets.register<ClientPacket> {
    opcode = 33
    length = 6
}
packets.register<ClientPacket> {
    opcode = 34
    length = 8
}
packets.register<ClientPacket> {
    opcode = 35
    length = -1
}
packets.register<ClientPacket> {
    opcode = 36
    length = 3
}
packets.register<ClientPacket> {
    opcode = 37
    length = 7
}
packets.register<ClientPacket> {
    opcode = 38
    length = 2
}
packets.register<ClientPacket> {
    opcode = 39
    length = 8
}
packets.register<ClientPacket> {
    opcode = 40
    length = 11
}
packets.register<ClientPacket> {
    opcode = 41
    length = 7
}
packets.register<ClientPacket> {
    opcode = 42
    length = 8
}
packets.register<ClientPacket> {
    opcode = 43
    length = 15
}
packets.register<ClientPacket> {
    opcode = 44
    length = 7
}
packets.register<ClientPacket> {
    opcode = 45
    length = 3
}
packets.register<ClientPacket> {
    opcode = 46
    length = 3
}
packets.register<ClientPacket> {
    opcode = 47
    length = 8
}
packets.register<ClientPacket> {
    opcode = 48
    length = 4
}
packets.register<ClientPacket> {
    opcode = 49
    length = -1
}
packets.register<ClientPacket> {
    opcode = 50
    length = 2
}
packets.register<ClientPacket> {
    opcode = 51
    length = 8
}
packets.register<ClientPacket> {
    opcode = 52
    length = 3
}
packets.register<ClientPacket> {
    opcode = 53
    length = 11
}
packets.register<ClientPacket> {
    opcode = 54
    length = 8
}
packets.register<ClientPacket> {
    opcode = 55
    length = -1
}
packets.register<ClientPacket> {
    opcode = 56
    length = 8
}
packets.register<ClientPacket> {
    opcode = 58
    length = 0
}
packets.register<ClientPacket> {
    opcode = 59
    length = -1
}
packets.register<ClientPacket> {
    opcode = 60
    length = 4
}
packets.register<EventAppletFocus> {
    opcode = 61
    length = 1
}
packets.register<ClientPacket> {
    opcode = 62
    length = 9
}
packets.register<ClientPacket> {
    opcode = 63
    length = 9
}
packets.register<ClientPacket> {
    opcode = 64
    length = 0
}
packets.register<ClientPacket> {
    opcode = 65
    length = 8
}
packets.register<ClientPacket> {
    opcode = 67
    length = 0
}
packets.register<ClientPacket> {
    opcode = 68
    length = 13
}
packets.register<ClientPacket> {
    opcode = 69
    length = 7
}
packets.register<ClientPacket> {
    opcode = 70
    length = 3
}
packets.register<ClientPacket> {
    opcode = 71
    length = -2
}
packets.register<ClientPacket> {
    opcode = 72
    length = 7
}
packets.register<ClientPacket> {
    opcode = 73
    length = 0
}
packets.register<ClientPacket> {
    opcode = 74
    length = -1
}
packets.register<ClientPacket> {
    opcode = 75
    length = 14
}
packets.register<ClientPacket> {
    opcode = 76
    length = 10
}
packets.register<ClientPacket> {
    opcode = 77
    length = -1
}
packets.register<ClientPacket> {
    opcode = 78
    length = 8
}
packets.register<ClientPacket> {
    opcode = 79
    length = -1
}
packets.register<ClientPacket> {
    opcode = 80
    length = 7
}
packets.register<ClientPacket> {
    opcode = 81
    length = 2
}
packets.register<ClientPacket> {
    opcode = 82
    length = -1
}
packets.register<ClientPacket> {
    opcode = 83
    length = 8
}
packets.register<ClientPacket> {
    opcode = 84
    length = 7
}
packets.register<ClientPacket> {
    opcode = 85
    length = 2
}
packets.register<EventMouseMove> {
    opcode = 86
    length = -1
}
packets.register<ClientPacket> {
    opcode = 87
    length = -1
}
packets.register<ClientPacket> {
    opcode = 88
    length = 3
}
packets.register<ClientPacket> {
    opcode = 89
    length = 0
}
packets.register<ClientPacket> {
    opcode = 90
    length = 3
}
packets.register<ClientPacket> {
    opcode = 91
    length = -2
}
packets.register<ClientPacket> {
    opcode = 92
    length = 8
}
packets.register<ClientPacket> {
    opcode = 93
    length = -1
}
packets.register<ClientPacket> {
    opcode = 94
    length = -1
}
packets.register<ClientPacket> {
    opcode = 95
    length = 3
}
packets.register<ClientPacket> {
    opcode = 96
    length = 8
}
packets.register<ClientPacket> {
    opcode = 97
    length = -1
}
packets.register<ClientPacket> {
    opcode = 98
    length = 8
}
packets.register<ClientPacket> {
    opcode = 99
    length = -1
}
packets.register<ClientPacket> {
    opcode = 100
    length = 7
}
packets.register<ClientPacket> {
    opcode = 101
    length = 3
}
packets.register<ClientPacket> {
    opcode = 102
    length = 15
}
packets.register<ClientPacket> {
    opcode = 103
    length = 11
}
packets.register<ClientPacket> {
    opcode = 104
    length = 8
}
packets.register<ClientPacket> {
    opcode = 105
    length = 11
}
