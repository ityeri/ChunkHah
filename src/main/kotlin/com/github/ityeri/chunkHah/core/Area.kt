package com.github.ityeri.chunkHah.core

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.UUID

class Area(
    var playerUUID: UUID?, val areaManager: AreaManager,
    var x: Int, var z: Int,
) {

    constructor(
        player: Player?, areaManager: AreaManager,
        x: Int, z: Int):
            this(player?.uniqueId, areaManager, x, z)

    val isOwned: Boolean get() = playerUUID != null

    val isPlayerOnline: Boolean?
        get() {
            playerUUID?.let { return Bukkit.getPlayer(it) != null }
            return null
        }

    val player: Player?
        get() {
            playerUUID?.let { return Bukkit.getPlayer(playerUUID!!) }
            return null
        }
}