package org.rsmod.plugins.api.model.ui.gameframe

import org.rsmod.plugins.api.cache.name.ui.ComponentNameMap
import org.rsmod.plugins.api.cache.name.ui.UserInterfaceNameMap

@DslMarker
private annotation class BuilderDsl

@BuilderDsl
class GameFrameBuilder(
    var type: GameFrameType? = null,
    var topLevel: String = "",
    private val components: MutableMap<String, GameFrameNameComponent> = mutableMapOf()
) {

    fun component(init: GameframeComponentBuilder.() -> Unit) {
        val builder = GameframeComponentBuilder().apply(init)
        val component = builder.build()
        components[component.target] = component
    }

    fun build(interfaceMap: UserInterfaceNameMap, componentMap: ComponentNameMap): GameFrame {
        val type = type ?: error("Gameframe type must be set")
        val topLevel = interfaceMap.getValue(topLevel)
        val components = components.toComponentMap(interfaceMap, componentMap)
        return GameFrame(type, topLevel, components)
    }

    private fun Map<String, GameFrameNameComponent>.toComponentMap(
        interfaces: UserInterfaceNameMap,
        components: ComponentNameMap
    ): GameFrameComponentMap {
        val map = entries.associate { entry ->
            val component = entry.value
            val name = component.name
            val inter = interfaces.getValue(component.inter)
            val target = components.getValue(component.target)
            name to GameFrameComponent(component.name, inter, target)
        }
        return GameFrameComponentMap(LinkedHashMap(map))
    }
}

@BuilderDsl
class GameframeComponentBuilder(
    var name: String = "",
    var inter: String = "",
    var target: String = ""
) {

    fun build(): GameFrameNameComponent {
        check(name.isNotBlank()) { "Component name must be set" }
        return GameFrameNameComponent(
            name = name,
            inter = inter,
            target = target
        )
    }
}
