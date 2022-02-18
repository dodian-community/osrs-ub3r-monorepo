import org.rsmod.game.model.mob.Player
import org.rsmod.game.model.ui.Component
import org.rsmod.plugins.api.model.mob.player.runClientScript
import org.rsmod.plugins.api.model.mob.player.setVarbit
import org.rsmod.plugins.api.model.ui.InterfaceEvent
import org.rsmod.plugins.api.model.ui.setComponentEvents
import org.rsmod.plugins.api.onOpenOverlay
import org.rsmod.plugins.api.onOpenTopLevel

onOpenTopLevel(inter("game_frame_fixed")) {
    player.setDisplayMode(0)
}

onOpenTopLevel(inter("game_frame_resize_classic")) {
    player.setDisplayMode(1)
}

onOpenTopLevel(inter("game_frame_resize_modern")) {
    player.setDisplayMode(2)
}

onOpenOverlay(inter("settings_tab")) {
    player.setComponentEvents(component("client_layout_dropdown"), 1..4, InterfaceEvent.BUTTON1)

    player.setComponentEvents(Component(116, 41), 0..21, InterfaceEvent.BUTTON1)
    player.setComponentEvents(Component(116, 69), 0..21, InterfaceEvent.BUTTON1)
    player.setComponentEvents(Component(116, 81), 1..5, InterfaceEvent.BUTTON1)
    player.setComponentEvents(Component(116, 84), 1..3, InterfaceEvent.BUTTON1)
    player.setComponentEvents(Component(116, 23), 0..21, InterfaceEvent.BUTTON1)
    player.setComponentEvents(Component(116, 83), 1..5, InterfaceEvent.BUTTON1)
    player.setComponentEvents(Component(116, 55), 0..21, InterfaceEvent.BUTTON1)
}

fun Player.setDisplayMode(mode: Int) {
    displayMode = mode

    when (mode) {
        1 -> setVarbit(varbit("side_stones_arrangement"), 0)
        2 -> setVarbit(varbit("side_stones_arrangement"), 1)
    }

    runClientScript(script("display_mode"), displayMode)
}
