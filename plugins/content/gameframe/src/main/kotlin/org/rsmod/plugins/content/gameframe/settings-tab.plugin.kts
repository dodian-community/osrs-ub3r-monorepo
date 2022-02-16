import com.github.michaelbull.logging.InlineLogger
import org.rsmod.game.model.enum.type.EnumType
import org.rsmod.game.model.ui.Component
import org.rsmod.game.model.ui.type.InterfaceType
import org.rsmod.plugins.api.*
import org.rsmod.plugins.api.model.mob.player.sendMessage
import org.rsmod.plugins.api.model.ui.closeOverlay
import org.rsmod.plugins.api.model.ui.closeTopLevel
import org.rsmod.plugins.api.model.ui.gameframe.*
import org.rsmod.plugins.api.model.ui.openTopLevel
import org.rsmod.plugins.api.protocol.packet.server.IfMoveSub

private val logger = InlineLogger()

val frames: GameFrameList by inject()

onButton(component("client_layout_dropdown")) {
    if (player.displayMode == child - 1) return@onButton

    player.sendMessage("We're now gonna change to display mode: $child")

    val topLevel: InterfaceType
    val newModeEnum: EnumType

    when (child) {
        2 -> {
            topLevel = inter("game_frame_resize_classic")
            newModeEnum = enum("game_frame_resize_classic")
        }
        3 -> {
            topLevel = inter("game_frame_resize_modern")
            newModeEnum = enum("game_frame_resize_modern")
        }
        else -> {
            topLevel = inter("game_frame_fixed")
            newModeEnum = enum("game_frame_fixed")
        }
    }

    val oldModeEnum = when (player.displayMode + 1) {
        2 -> enum("game_frame_resize_classic")
        3 -> enum("game_frame_resize_modern")
        else -> enum("game_frame_fixed")
    }

    player.ui.topLevel.forEach {
        player.closeTopLevel(it)
    }
    player.openTopLevel(topLevel)

    oldModeEnum.intValues.forEachIndexed { index, it ->
        val oldParent = it shr 16
        val oldChild = it and 0xffff
        val newPacked = newModeEnum.intValues[index]
        val newParent = newPacked shr 16
        val newChild = newPacked and 0xffff

        val overlayInterface = player.ui.overlays[Component(it)]
        player.ui.overlays.remove(Component(it))
        if (overlayInterface != null) {
            player.ui.overlays[Component(newPacked)] = overlayInterface
        }

        if (oldParent shl 16 or oldChild != -1 && newParent shl 16 or newChild != -1) {
            val ifMoveSub = IfMoveSub(from = (oldParent shl 16) or oldChild, to = (newParent shl 16) or newChild)
            logger.debug { "Sending $ifMoveSub" }
            player.write(ifMoveSub)
        }
    }
}

onObj(obj("null_10356"), "Bank") {
    logger.info { "Banking?" }
    player.logout()
}
