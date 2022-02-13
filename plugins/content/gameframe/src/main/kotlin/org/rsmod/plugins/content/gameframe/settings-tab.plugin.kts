import com.github.michaelbull.logging.InlineLogger
import org.rsmod.game.model.mob.Player
import org.rsmod.plugins.api.*
import org.rsmod.plugins.api.model.mob.player.runClientScript
import org.rsmod.plugins.api.model.mob.player.setVarbit
import org.rsmod.plugins.api.model.ui.*
import org.rsmod.plugins.api.model.ui.gameframe.*
import org.rsmod.plugins.api.protocol.packet.server.IfMoveSub

private val logger = InlineLogger()

val frames: GameFrameList by inject()

onButton(component("client_layout_dropdown")) {
    val fixed = frames.getValue(GameFrameFixed)
    val classic = frames.getValue(GameFrameClassic)
    val modern = frames.getValue(GameFrameModern)

    val gameFrame = when (child) {
        2 -> classic
        3 -> modern
        else -> fixed
    }

    if (player.displayMode == child) return@onButton

    val newMode = child
    val oldMode = player.displayMode
    player.displayMode = newMode

    player.openTopLevel(gameFrame.topLevel)

    when (child) {
        1 -> {
            player.closeTopLevel(classic.topLevel)
            player.closeTopLevel(modern.topLevel)
        }
        2 -> {
            player.closeTopLevel(fixed.topLevel)
            player.closeTopLevel(modern.topLevel)
        }
        3 -> {
            player.closeTopLevel(fixed.topLevel)
            player.closeTopLevel(classic.topLevel)
        }
    }

    val fromParent = when (oldMode) {
        1 -> inter("game_frame_classic").id
        2 -> inter("game_frame_modern").id
        else -> inter("game_frame_fixed").id
    }
    val toParent = gameFrame.topLevel.id

    gameFrame.components.values.forEach { component ->
        player.ui.overlays.filterValues { inter ->
            inter.id == component.inter.id
        }.values.forEach {
            player.write(IfMoveSub(from = (fromParent shl 16) or it.id, to = (toParent shl 16) or it.id))
        }
        player.ui.modals.filterValues { inter ->
            inter.id == component.inter.id
        }.values.forEach {
            player.write(IfMoveSub(from = (fromParent shl 16) or it.id, to = (toParent shl 16) or it.id))
        }
    }
}

onObj(obj("bank_booth_10356"), "Bank") {
    logger.info { "Banking?" }
    player.logout()
}
