package com.github.ityeri.chunkHah.core

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.security.cert.TrustAnchor
import java.util.UUID

class Area(
    val playerUUID: UUID, val areaManager: AreaManager,
    var x: Int, var z: Int, var enabled: Boolean = true
) {

    constructor(
        player: Player, areaManager: AreaManager,
        x: Int, z: Int):
            this(player.uniqueId, areaManager, x, z)

    val width: Int
        get() = areaManager.areaWidth
    val depth: Int
        get() = areaManager.areaDepth


    val minX: Int
        get() = x * width
    val maxX: Int
        get() = (x + 1) * width

    val minZ: Int
        get() = z * depth
    val maxZ: Int
        get() = (z + 1) * depth


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



    fun update() {
        // TODO
    }

}