package com.github.ityeri.chunkHah.core

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent

class PlayerInteractionHandler(val area: Area) : Listener {

    @EventHandler
    fun onPlayerInteractHandler(event: PlayerInteractEvent) {

        if (event.player != area.player) { return }
        if (event.clickedBlock == null) { return }

        val block = event.clickedBlock!!

        if (!(area.minX <= block.x && block.x < area.maxX &&
                    area.minZ <= block.z && block.z < area.maxZ)) {

            event.isCancelled = true
        }
    }
}