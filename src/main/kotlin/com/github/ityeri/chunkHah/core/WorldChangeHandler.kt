package com.github.ityeri.chunkHah.core

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.world.PortalCreateEvent
import org.bukkit.event.world.PortalCreateEvent.CreateReason
import kotlin.random.Random
import kotlin.random.nextInt

class WorldChangeHandler(val area: Area) : Listener {

    fun runTaskNextTick(block: () -> Unit) {
        Bukkit.getScheduler().runTaskLater(
            area.areaManager!!.plugin,
            Runnable { block() },
            1L
        )
    }

    fun onOverToNether() {

        val xCheckingOrder = (area.minX until area.maxX).shuffled()
        val yCheckingOrder = (0 until 256).shuffled()
        val zCheckingOrder = (area.minZ until area.maxZ).shuffled()

        for (spawnBlockZ in zCheckingOrder) {
            for (spawnBlockY in yCheckingOrder) {
                for (spawnBlockX in xCheckingOrder) {

                    val lowerBlock = area.player!!.world.getBlockAt(spawnBlockX, spawnBlockY, spawnBlockZ)
                    val upperBlock = area.player!!.world.getBlockAt(spawnBlockX, spawnBlockY + 1, spawnBlockZ)

                    if (
                        lowerBlock.type == Material.NETHER_PORTAL &&
                        upperBlock.type == Material.NETHER_PORTAL
                    ) {

                        runTaskNextTick {
                            area.player!!.teleport(
                                Location(
                                    area.player!!.world,
                                    spawnBlockX + 0.5,
                                    spawnBlockY.toDouble(),
                                    spawnBlockZ + 0.5,
                                )
                            )
                        }
                        return
                    }

                }
            }
        }

        while (true) {

            val spawnBlockX = Random.nextInt(area.minX, area.maxX)
            val spawnBlockY = Random.nextInt(0, 128)
            val spawnBlockZ = Random.nextInt(area.minZ, area.maxZ)

            val groundBlock = area.player!!.world.getBlockAt(
                spawnBlockX, spawnBlockY - 1, spawnBlockZ)

            val lowerBlock = area.player!!.world.getBlockAt(
                spawnBlockX, spawnBlockY, spawnBlockZ)

            val upperBlock = area.player!!.world.getBlockAt(
                spawnBlockX, spawnBlockY + 1, spawnBlockZ)

            if (groundBlock.isSolid &&
                lowerBlock.isEmpty && upperBlock.isEmpty) {

                runTaskNextTick {
                    area.player!!.teleport(
                        Location(
                            area.player!!.world,
                            spawnBlockX + 0.5,
                            spawnBlockY + 0.5,
                            spawnBlockZ + 0.5
                        )
                    )
                }
                return
            }

        }

    }

    @EventHandler
    fun onPlayerChangeWorld(event: PlayerChangedWorldEvent) {

        Bukkit.getLogger().info("체크 전")

        if (event.player != area.player) { return }

        Bukkit.getLogger().info("체크 후")

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
                    runTaskNextTick {
                        area.player!!.teleport(
                            Location(
                                toWorld,
                                spawnBlockX + 0.5,
                                spawnBlockY + 0.5,
                                spawnBlockZ + 0.5
                            )
                        )
                    }

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