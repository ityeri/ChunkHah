package com.github.ityeri.chunkHah.core

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.world.PortalCreateEvent
import org.bukkit.event.world.PortalCreateEvent.CreateReason
import kotlin.random.Random

class WorldChangeHandler(val area: Area) : Listener {

    fun onOverToNether() {
        TODO()
    }

    @EventHandler
    fun onPlayerChangeWorld(event: PlayerChangedWorldEvent) {

        if (event.player != area.player) { return }

        val fromWorld = event.from
        val toWorld = area.player!!.world

        // 오버 -> 네더
        if (fromWorld.environment == World.Environment.NORMAL &&
            toWorld.environment == World.Environment.NETHER) {
            onOverToNether()
        }

        // 네더 -> 오버
        else if (fromWorld.environment == World.Environment.NETHER &&
            toWorld.environment == World.Environment.NORMAL) {

            while (true) {

                val spawnBlockX = Random.nextInt(area.minX, area.maxX)
                val spawnBlockZ = Random.nextInt(area.minZ, area.maxZ)

                val groundBlock = toWorld.getHighestBlockAt(spawnBlockX, spawnBlockZ)
                val spawnBlockY = groundBlock.y + 1

                if (groundBlock.isSolid) {
                    Bukkit.getScheduler().runTaskLater(area.areaManager!!.plugin, Runnable {
                        area.player!!.teleport(
                            Location(
                            toWorld,
                            spawnBlockX + 0.5,
                            spawnBlockY + 0.5,
                            spawnBlockZ + 0.5
                        )
                        )
                    }, 1L)

                    break
                }

            }

        }
    }

    @EventHandler
    fun onPortalCreate(event: PortalCreateEvent) {
        if (event.reason == CreateReason.NETHER_PAIR) {
            event.isCancelled = true
        }
    }
}