

package com.github.ityeri.chunkHah.core

import net.kyori.adventure.text.Component
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
    var x: Int, var z: Int, var isBind: Boolean = true
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
    }

    fun disable() {
        if (!isEnabled) {
            throw IllegalStateException("이미 비활성화 되어 있습니다")
        }
        isEnabled = false

        HandlerList.unregisterAll(this)
    }



    fun update() {
        if (!isBind) { return }
        if (isPlayerOnline) {
            playerPositionCheck()
        }
    }

    fun playerPositionCheck() {
        // x- x+ z- z+

        val newLocation = player!!.location.clone()
        val newVelocity = player!!.velocity

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



    @EventHandler
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        if (event.player.uniqueId != playerUUID) { return }
        if (event.isBedSpawn || event.isAnchorSpawn) { return }

        val respawnX = Random.nextInt(minX, maxX) + 0.5
        val respawnZ = Random.nextInt(minX, maxX) + 0.5

        val respawnY = event.respawnLocation.world.getHighestBlockAt(
            respawnX.toInt(), respawnZ.toInt()
        ).y + 1.5

        event.respawnLocation = Location(event.respawnLocation.world,
            respawnX, respawnY, respawnZ
        )
    }

    @EventHandler
    fun onPlayerChangeWorld(event: PlayerChangedWorldEvent) {

        if (event.player != player) { return }

        val fromWorld = event.from
        val toWorld = event.player.world

        // 오버 -> 네더
        if (fromWorld.environment == World.Environment.NORMAL &&
            toWorld.environment == World.Environment.NETHER) {

            val spawnBlockX = Random.nextInt(minX, maxX)
            val spawnBlockY = Random.nextInt(1, 120)
            val spawnBlockZ = Random.nextInt(minZ, maxZ)

            toWorld.buildGlassCase(spawnBlockX, spawnBlockY, spawnBlockZ)


        }
    }

    @EventHandler
    fun onPortalCreate(event: PortalCreateEvent) {
        if (event.reason == CreateReason.NETHER_PAIR) {
            event.isCancelled = true
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