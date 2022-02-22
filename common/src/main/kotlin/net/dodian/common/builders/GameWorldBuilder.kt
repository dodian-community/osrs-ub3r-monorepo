package net.dodian.common.builders

import net.dodian.common.models.GameWorld
import net.dodian.common.models.WorldLocation
import net.dodian.common.models.WorldProperty

class GameWorldBuilder(
    val id: Int,
    var host: String = "127.0.0.1",
    var activity: String = "-",
    var location: WorldLocation = WorldLocation.GERMANY,
    var properties: List<WorldProperty> = listOf()
) {

    fun build() = GameWorld(
        id = id,
        host = host,
        activity = activity,
        locationId = location.id,
        properties = properties.sumOf { it.id }
    )
}
