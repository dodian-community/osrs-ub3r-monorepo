import com.github.michaelbull.logging.InlineLogger
import org.rsmod.game.model.enum.type.EnumType
import org.rsmod.game.model.mob.Player
import org.rsmod.game.model.ui.Component
import org.rsmod.game.model.ui.type.InterfaceType
import org.rsmod.plugins.api.*
import org.rsmod.plugins.api.model.mob.player.getVarbit
import org.rsmod.plugins.api.model.mob.player.setVarbit
import org.rsmod.plugins.api.model.ui.*
import org.rsmod.plugins.api.protocol.packet.server.IfMoveSub

private val logger = InlineLogger()

data class DisplayMode(
    val topLevel: InterfaceType,
    val enum: EnumType
)

fun newDisplayMode(child: Int): DisplayMode = when (child) {
    2 -> DisplayMode(
        topLevel = inter("game_frame_resize_classic"),
        enum = enum("game_frame_resize_classic")
    )
    3 -> DisplayMode(
        topLevel = inter("game_frame_resize_modern"),
        enum = enum("game_frame_resize_modern")
    )
    else -> DisplayMode(
        topLevel = inter("game_frame_fixed"),
        enum = enum("game_frame_fixed")
    )
}

onButton(component("client_layout_dropdown")) {
    if (player.displayMode == child - 1) return@onButton

    val displayMode = newDisplayMode(child)

    val oldModeEnum = when (player.displayMode + 1) {
        2 -> enum("game_frame_resize_classic")
        3 -> enum("game_frame_resize_modern")
        else -> enum("game_frame_fixed")
    }

    player.topLevel().forEach {
        player.closeTopLevel(it)
    }
    player.ui.topLevel.clear()
    player.openTopLevel(displayMode.topLevel)

    player.moveOverlays(oldModeEnum, displayMode.enum)
}

fun Player.moveOverlays(oldModeEnum: EnumType, newModeEnum: EnumType) {
    ui.overlays = ui.overlays.map { (component, ui) ->
        val oldParent = component.interfaceId
        val oldChild = component.child

        val packedResizable = oldModeEnum.keyByPackedValue(component.packed)
        val newPacked = newModeEnum.values[packedResizable] as? Int

        if (newPacked != null) {
            val newParent = newPacked shr 16
            val newChild = newPacked and 0xffff

            val from = oldParent shl 16 or oldChild
            val to = newParent shl 16 or newChild

            if (to != -1 && from != -1)
                write(IfMoveSub(from = from, to = to))

            Component(newPacked) to ui
        } else component to ui
    }.toMap().toMutableMap()
}

val pvpSpecOrbVarbit = varbit("pvp_spec_orb")
val inWildernessVarbit = varbit("in_wilderness")

onObj(obj("null_10356"), "Bank") {
    val value = player.getVarbit(pvpSpecOrbVarbit)
    logger.info { "value? $value" }
    player.setVarbit(pvpSpecOrbVarbit, value + 2)
}

onLogin {
    player.setVarbit(inWildernessVarbit, 1)
    player.setVarbit(pvpSpecOrbVarbit, 0)
}

onButton(component("all_settings_button")) {
    player.openOverlay(inter("all_settings"), component("main_screen"), InterfaceClickMode.Enabled)
}
