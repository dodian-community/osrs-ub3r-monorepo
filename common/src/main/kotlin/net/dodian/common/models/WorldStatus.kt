package net.dodian.common.models

enum class WorldStatus {
    ONLINE,
    OFFLINE
    ;

    override fun toString() = this.name.lowercase()
}
