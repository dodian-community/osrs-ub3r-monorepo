package org.rsmod.plugins.api.model.ui.gameframe

import com.google.common.base.MoreObjects
import org.rsmod.game.model.ui.type.InterfaceType

data class GameFrame(
    val type: GameFrameType,
    val topLevel: InterfaceType,
    val components: GameFrameComponentMap
) : Map<String, GameFrameComponent> by components {

    override fun toString() = MoreObjects.toStringHelper(this)
        .add("type", type::class.simpleName)
        .add("topLevel", topLevel)
        .add("components", components)
        .toString()
}
