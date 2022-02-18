package org.rsmod.plugins.content.gameframe

import org.rsmod.game.config.GameConfig
import org.rsmod.game.model.mob.Player
import org.rsmod.game.model.ui.Component
import org.rsmod.plugins.api.model.mob.player.*
import org.rsmod.plugins.api.model.ui.gameframe.GameFrameList
import org.rsmod.plugins.api.model.ui.gameframe.GameFrameResizeClassic
import org.rsmod.plugins.api.model.ui.openGameFrame
import org.rsmod.plugins.api.onEarlyLogin
import org.rsmod.plugins.api.onLogin
import org.rsmod.plugins.api.protocol.packet.server.IfSetHide
import org.rsmod.plugins.api.protocol.packet.server.ResetAnims
import org.rsmod.plugins.api.protocol.packet.server.ResetClientVarCache

val config: GameConfig by inject()
val frames: GameFrameList by inject()

val varp1 = varp("varp_1055")
val varp2 = varp("varp_1737")

onLogin {
    player.sendLogin()
}

onEarlyLogin {
    player.openGameFrame(frames.getValue(GameFrameResizeClassic))
    player.sendEarlyLogin()
}

fun Player.sendLogin() {
    sendMessage("Welcome to ${config.name}.", MessageType.WELCOME)
    write(ResetClientVarCache)
    write(ResetAnims)

    // Hide PVP Skull
    write(IfSetHide(Component(161, 3).packed, true))
    write(IfSetHide(Component(164, 3).packed, true))
    write(IfSetHide(Component(548, 28).packed, true))
}

fun Player.sendEarlyLogin() {
    setVarp(varp1, 1)
    setVarp(varp2, -1)
    runClientScript(script("skill_guide_sidepanelop"), 1)
    sendRunEnergy()
}
