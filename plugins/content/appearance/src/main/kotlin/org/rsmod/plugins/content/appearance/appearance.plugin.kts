package org.rsmod.plugins.content.appearance

import com.github.michaelbull.logging.InlineLogger
import org.rsmod.game.model.domain.Appearance
import org.rsmod.game.model.domain.Direction
import org.rsmod.game.model.mob.Player
import org.rsmod.plugins.api.model.mob.faceDirection
import org.rsmod.plugins.api.model.mob.player.updateAppearance
import org.rsmod.plugins.api.onEarlyLogin

val logger = InlineLogger()

onEarlyLogin {
    player.setAndUpdateAppearance()
    player.faceDirection(Direction.South)
}

fun Player.setAndUpdateAppearance() {
    logger.debug { "Setting appearance for entity: ${entity.username}" }
    if (entity.appearance === Appearance.ZERO) {
        entity.appearance = AppearanceConstants.DEFAULT_APPEARANCE
    }
    updateAppearance()
}
