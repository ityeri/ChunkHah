package com.github.ityeri.chunkHah.core

import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.util.UUID

class AreaManager(
    var areaWidth: Int, var areaDepth: Int
) {

    private val playerAreaMap: MutableMap<UUID, Area> = mutableMapOf()

    fun enable() {

    }

    fun disable() {

    }

    fun getAllArea(): Map<UUID, Area> = playerAreaMap

    fun addArea(player: Player, area: Area) { playerAreaMap[player.uniqueId] = area }
    fun getArea(player: OfflinePlayer): Area? = playerAreaMap[player.uniqueId]

}

