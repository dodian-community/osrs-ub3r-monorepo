package org.rsmod.plugins.api.model.ui.gameframe

import org.rsmod.game.model.ui.type.ComponentType
import org.rsmod.game.model.ui.type.InterfaceType

data class GameFrameComponent(val name: String, val inter: InterfaceType, val target: ComponentType)

data class GameFrameNameComponent(val name: String, val inter: String, val target: String)

class GameFrameComponentMap(
    private val components: LinkedHashMap<String, GameFrameComponent>
) : Map<String, GameFrameComponent> by components {

    fun add(component: GameFrameComponent) {
        components[component.name] = component
    }
}
