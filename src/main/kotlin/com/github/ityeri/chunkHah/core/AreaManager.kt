package com.github.ityeri.chunkHah.core

import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin
import java.util.UUID

class AreaManager(
    val plugin: JavaPlugin,
    var areaWidth: Int, var areaDepth: Int
) : Listener {

    private val playerAreaMap: MutableMap<UUID, Area> = mutableMapOf()

    fun enable() {

    }

    fun disable() {

    }

    fun getAllArea(): Map<UUID, Area> = playerAreaMap

    fun addArea(player: Player, area: Area) { playerAreaMap[player.uniqueId] = area }
    fun getArea(player: OfflinePlayer): Area? = playerAreaMap[player.uniqueId]



    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {

    }

}

