package org.rsmod.plugins.api.protocol.codec.game

import io.netty.channel.Channel
import org.rsmod.game.message.ServerPacket
import org.rsmod.game.message.ServerPacketListener
import org.rsmod.plugins.api.protocol.packet.server.*

class ChannelMessageListener(
    private val channel: Channel
) : ServerPacketListener {

    private val validPackets = setOf(
        RebuildNormal::class,
        PlayerInfo::class,
        IfOpenTop::class,
        IfOpenSub::class,
        ResetClientVarCache::class,
        ResetAnims::class,
        IfSetText::class,
        //NpcInfoSmallViewport::class,
        UpdateStat::class,
        MessageGame::class,
        UpdateRunEnergy::class,
        VarpSmall::class,
        VarpLarge::class,
        RunClientScript::class
    )

    override fun write(packet: ServerPacket) {
        if (packet::class !in validPackets) return

        channel.write(packet)
    }

    override fun flush() {
        channel.flush()
    }

    override fun close() {
        channel.close()
    }
}
