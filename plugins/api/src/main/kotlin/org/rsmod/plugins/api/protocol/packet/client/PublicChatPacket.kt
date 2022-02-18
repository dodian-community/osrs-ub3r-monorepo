package org.rsmod.plugins.api.protocol.packet.client

import org.rsmod.game.cache.GameCache
import org.rsmod.game.event.impl.ChatMessage
import org.rsmod.game.event.impl.LocalChat
import org.rsmod.game.message.ClientPacket
import org.rsmod.game.message.ClientPacketHandler
import org.rsmod.game.model.client.Client
import org.rsmod.game.model.mob.Player
import org.rsmod.plugins.api.protocol.packet.update.PublicChat
import javax.inject.Inject

class GameChat(
    val effect: Int,
    val color: Int,
    val length: Int,
    val data: ByteArray,
    val type: Int
) : ClientPacket

class PublicChatHandler @Inject constructor(
    val cache: GameCache
) : ClientPacketHandler<GameChat> {

    override fun handle(client: Client, player: Player, packet: GameChat) {
        val decompressed = ByteArray(230)
        val huffman = cache.huffman()
        huffman.decompress(packet.data, decompressed, packet.length)

        val unpacked = String(decompressed, 0, packet.length)
        val type = ChatMessage.ChatType.values.firstOrNull { it.id == packet.type } ?: ChatMessage.ChatType.NONE
        val effect = ChatMessage.ChatEffect.values.firstOrNull { it.id == packet.effect } ?: ChatMessage.ChatEffect.NONE
        val color = ChatMessage.ChatColor.values.firstOrNull { it.id == packet.color } ?: ChatMessage.ChatColor.NONE

        val message = ChatMessage(unpacked, player.privileges.first().clientId, type, effect, color)

        // TODO: Message queue system?

        player.submitEvent(LocalChat(message))
        player.entity.updates.add(PublicChat(message))
    }
}
