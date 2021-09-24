package org.rsmod.plugins.api.protocol.packet.server

import org.rsmod.game.message.ServerPacket

data class ReflectionChecker(
    val something: Int
) : ServerPacket
