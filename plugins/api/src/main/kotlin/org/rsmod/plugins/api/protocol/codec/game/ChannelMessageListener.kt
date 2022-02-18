package org.rsmod.plugins.api.protocol.codec.game

import com.github.michaelbull.logging.InlineLogger
import io.netty.channel.Channel
import org.rsmod.game.message.ServerPacket
import org.rsmod.game.message.ServerPacketListener
import org.rsmod.plugins.api.protocol.packet.server.*

private val logger = InlineLogger()

class ChannelMessageListener(
    private val channel: Channel
) : ServerPacketListener {

    private val validPackets = setOf(
        RebuildNormal::class,
        PlayerInfo::class,
        IfOpenTop::class,
        IfOpenSub::class,
        IfMoveSub::class,
        ResetClientVarCache::class,
        ResetAnims::class,
        IfSetText::class,
        IfSetEvents::class,
        IfSetHide::class,
        NpcInfoSmallViewport::class,
        NpcInfoLargeViewport::class,
        UpdateStat::class,
        MessageGame::class,
        UpdateRunEnergy::class,
        VarpSmall::class,
        VarpLarge::class,
        RunClientScript::class
    )

    override fun write(packet: ServerPacket) {
        if (packet::class !in validPackets) return

        if (packet !is PlayerInfo)
            logger.trace { "Writing packet: $packet" }

        channel.write(packet)
    }

    override fun flush() {
        channel.flush()
    }

    override fun close() {
        channel.close()
    }
}
