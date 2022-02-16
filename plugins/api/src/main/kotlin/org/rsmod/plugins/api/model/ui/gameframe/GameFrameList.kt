package org.rsmod.plugins.api.model.ui.gameframe

import com.github.michaelbull.logging.InlineLogger
import org.rsmod.plugins.api.cache.name.ui.ComponentNameMap
import org.rsmod.plugins.api.cache.name.ui.UserInterfaceNameMap
import javax.inject.Inject

private val logger = InlineLogger()

class GameFrameList(
    private val interfaces: UserInterfaceNameMap,
    private val components: ComponentNameMap,
    private val frames: MutableMap<GameFrameType, GameFrame>
) : Map<GameFrameType, GameFrame> by frames {

    @Inject
    constructor(
        interfaces: UserInterfaceNameMap,
        components: ComponentNameMap
    ) : this(interfaces, components, mutableMapOf())

    fun register(frame: GameFrame) {
        check(!frames.containsKey(frame.type)) { "Game frame with type already exists (type=${frame.type})" }
        logger.debug { "Register game frame (frame=$frame)" }
        frames[frame.type] = frame
    }

    fun register(init: GameFrameBuilder.() -> Unit) {
        val builder = GameFrameBuilder().apply(init)
        val gameFrame = builder.build(interfaces, components)
        register(gameFrame)
    }
}
