package com.github.ityeri.chunkHah.core

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.UUID

class Area(
    val playerUUID: UUID, val areaManager: AreaManager,
    var x: Int, var z: Int,
) {

    constructor(
        player: Player, areaManager: AreaManager,
        x: Int, z: Int):
            this(player.uniqueId, areaManager, x, z)

    val isPlayerOnline: Boolean
        get() {
            return Bukkit.getPlayer(playerUUID) != null
        }

    val player: Player?
        get() {
            return Bukkit.getPlayer(playerUUID)
        }

    fun whenPlayerOnline(block: (Player) -> Unit) {
        if (isPlayerOnline) {
            block(player!!)
        }
    }
}