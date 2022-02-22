package net.dodian.common.models

import com.fasterxml.jackson.annotation.JsonIgnore
import java.util.Date

data class GameWorld(
    var id: Int,
    val host: String,
    var activity: String,
    @JsonIgnore var properties: Int,
    @JsonIgnore val locationId: Int,
    var population: Int = 0,
    var status: WorldStatus = WorldStatus.OFFLINE,
    var lastResponse: Long = -1L
) {

    private val isMembersOnly = WorldProperty.MEMBERS_ONLY.id and properties != 0
    private val isPVP = WorldProperty.PVP.id and properties != 0
    private val isBeta = WorldProperty.BETA.id and properties != 0
    private val isDeadman = WorldProperty.DEADMAN.id and properties != 0

    var location = WorldLocation.values().single {
        it.id == locationId
    }.friendlyName

    val flags = mutableListOf<WorldProperty>()
        .addIf(isMembersOnly, WorldProperty.MEMBERS_ONLY)
        .addIf(isPVP, WorldProperty.PVP)
        .addIf(isBeta, WorldProperty.BETA)
        .addIf(isDeadman, WorldProperty.DEADMAN)

    private fun MutableList<WorldProperty>.addIf(condition: Boolean, element: WorldProperty): MutableList<WorldProperty> {
        if (condition) this.add(element)
        return this
    }
}
