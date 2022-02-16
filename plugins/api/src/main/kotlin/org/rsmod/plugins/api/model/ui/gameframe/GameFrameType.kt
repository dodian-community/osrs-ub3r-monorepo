package org.rsmod.plugins.api.model.ui.gameframe

open class GameFrameType(val id: Int)

object GameFrameFixed : GameFrameType(1)
object GameFrameResizeClassic : GameFrameType(2)
object GameFrameResizeModern : GameFrameType(3)
