package com.github.ityeri.chunkHah.core

import com.github.ityeri.chunkHah.core.serializer.AreaLoader
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin
import java.util.UUID

class AreaManager(
    val plugin: JavaPlugin,
    var areaWidth: Int, var areaDepth: Int
) : Listener {

    private val playerAreaMap: MutableMap<UUID, Area> = mutableMapOf()
    var isEnabled = false
    private var updateTaskId: Int? = null

    val areaLoader = AreaLoader(this)

    var areaInnerBlank = 0.01
    var repulsiveForce = 0.1

    fun enable() {
        if (isEnabled) { throw IllegalStateException("이미 활성화 되어 있습니다") }
        isEnabled = true

        Bukkit.getPluginManager().registerEvents(this, plugin)

        updateTaskId = Bukkit.getScheduler().runTaskTimer(plugin, Runnable {
            playerAreaMap.forEach { it.value.update() }
        }, 0L, 1L).taskId
    }

    fun disable() {
        if (!isEnabled) { throw IllegalStateException("이미 비활성화 되어 있습니다") }
        isEnabled = false

        HandlerList.unregisterAll(this)
        Bukkit.getScheduler().cancelTask(updateTaskId!!)
    }

    fun getAllArea(): List<Area> = playerAreaMap.values.toList()

    fun removeAllArea() {
        playerAreaMap.clear()
    }

    fun addArea(area: Area) {
        area.areaManager = this
        playerAreaMap[area.playerUUID] = area }

    fun getArea(player: OfflinePlayer): Area? = playerAreaMap[player.uniqueId]



    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {

    }

}

