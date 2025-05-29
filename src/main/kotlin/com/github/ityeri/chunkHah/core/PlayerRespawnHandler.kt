package com.github.ityeri.chunkHah.core

import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerRespawnEvent
import kotlin.random.Random

class PlayerRespawnHandler(val area: Area) : Listener {

    @EventHandler
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        if (event.player.uniqueId != area.playerUUID) { return }
        if (event.isBedSpawn || event.isAnchorSpawn) { return }

        val respawnX = Random.nextInt(area.minX, area.maxX) + 0.5
        val respawnZ = Random.nextInt(area.minX, area.maxX) + 0.5

        val respawnY = event.respawnLocation.world.getHighestBlockAt(
            respawnX.toInt(), respawnZ.toInt()
        ).y + 1.5

        event.respawnLocation = Location(event.respawnLocation.world,
            respawnX, respawnY, respawnZ
        )
    }
}