package com.github.ityeri.chunkHah

import com.github.ityeri.chunkHah.core.AreaManager
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin


class ChunkHah : JavaPlugin() {

    val areaManager = AreaManager(8, 8)

    override fun onEnable() {
        Bukkit.getLogger().info("청크핳")
    }

    override fun onDisable() {

    }
}
