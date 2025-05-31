

package com.github.ityeri.chunkHah.core

import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.event.world.PortalCreateEvent
import org.bukkit.event.world.PortalCreateEvent.CreateReason
import java.util.UUID
import kotlin.random.Random

class Area(
    val playerUUID: UUID, var areaManager: AreaManager?,
    var x: Int, var z: Int, var isBind: Boolean = true,
    var isEnteredOver: Boolean = false,
    var isEnteredNether: Boolean = false,
    var isEnteredEnd: Boolean = false,
) : Listener {

    constructor(
        player: Player, areaManager: AreaManager,
        x: Int, z: Int):
            this(player.uniqueId, areaManager, x, z)

    constructor(
        playerUUID: UUID, x: Int, z: Int, isBind: Boolean = true
    ): this(playerUUID, null, x, z, isBind)

    var isEnabled: Boolean = false

    val width: Int
        get() = areaManager!!.areaWidth
    val depth: Int
        get() = areaManager!!.areaDepth


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

    val worldChangeHandler = WorldChangeHandler(this)
    val playerRespawnHandler = PlayerRespawnHandler(this)
    val playerInteractionHandler = PlayerInteractionHandler(this)
    val firstWorldEntryHandler = FirstWorldEntryHandler(this)

    fun whenPlayerOnline(block: (Player) -> Unit) {
        if (isPlayerOnline) {
            block(player!!)
        }
    }

    fun enable() {
        if (isEnabled) {
            throw IllegalStateException("이미 활성화 되어 있습니다")
        }
        isEnabled = true

        Bukkit.getPluginManager().registerEvents(this, areaManager!!.plugin)
        Bukkit.getPluginManager().registerEvents(worldChangeHandler, areaManager!!.plugin)
        Bukkit.getPluginManager().registerEvents(playerRespawnHandler, areaManager!!.plugin)
        Bukkit.getPluginManager().registerEvents(playerInteractionHandler, areaManager!!.plugin)
    }

    fun disable() {
        if (!isEnabled) {
            throw IllegalStateException("이미 비활성화 되어 있습니다")
        }
        isEnabled = false

        HandlerList.unregisterAll(this)
        HandlerList.unregisterAll(worldChangeHandler)
        HandlerList.unregisterAll(playerRespawnHandler)
        HandlerList.unregisterAll(playerInteractionHandler)
    }



    fun update() {
        if (!isEnabled) {
            throw IllegalStateException("영역이 활성화 된 후에 update 가 호출되어야 합니다")
        }

        if (isPlayerOnline && isBind) { playerPositionCheck() }

        if (isPlayerOnline) { firstWorldEntryHandler.update() }
    }

    fun playerPositionCheck() {
        // x- x+ z- z+

        val newLocation = player!!.location.clone()
        val newVelocity = player!!.velocity.clone()

        var isChecked = false

        // 플레이어가 이동할 좌표 계산
        if (player!!.x < minX) {
            newLocation.x = minX + areaManager!!.areaInnerBlank
            newVelocity.x = areaManager!!.repulsiveForce
            isChecked = true
        }
        else if (maxX < player!!.x) {
            newLocation.x = maxX - areaManager!!.areaInnerBlank
            newVelocity.x = -areaManager!!.repulsiveForce
            isChecked = true

        }

        if (player!!.z < minZ) {
            newLocation.z = minZ + areaManager!!.areaInnerBlank
            newVelocity.z = areaManager!!.repulsiveForce
            isChecked = true

        }
        else if (maxZ < player!!.z) {
            newLocation.z = maxZ - areaManager!!.areaInnerBlank
            newVelocity.z = -areaManager!!.repulsiveForce
            isChecked = true

        }

        if (isChecked) {
            player!!.teleport(newLocation)
            player!!.velocity = newVelocity
        }
    }

}



fun World.buildGlassCase(x: Int, y: Int, z: Int) {
    getBlockAt(x, y + 2, z).type = Material.GLASS

    getBlockAt(x - 1, y, z).type = Material.GLASS
    getBlockAt(x + 1, y, z).type = Material.GLASS
    getBlockAt(x, y, z - 1).type = Material.GLASS
    getBlockAt(x, y, z + 1).type = Material.GLASS

    getBlockAt(x - 1, y + 1, z).type = Material.GLASS
    getBlockAt(x + 1, y + 1, z).type = Material.GLASS
    getBlockAt(x, y + 1, z - 1).type = Material.GLASS
    getBlockAt(x, y + 1, z + 1).type = Material.GLASS

    getBlockAt(x, y - 1, z).type = Material.GLASS

    getBlockAt(x, y, z).type = Material.AIR
    getBlockAt(x, y + 1, z).type = Material.AIR

}