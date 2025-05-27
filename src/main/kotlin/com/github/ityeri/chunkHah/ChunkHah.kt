package com.github.ityeri.chunkHah

import com.github.ityeri.chunkHah.core.AreaManager
import com.github.ityeri.chunkHah.userinterface.UserInterface
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin


class ChunkHah : JavaPlugin() {

    val areaManager = AreaManager(this, 8, 8)
    val userInterface = UserInterface(areaManager)

    override fun onEnable() {
        Bukkit.getServer().sendMessage(Component.text("청크핳"))
        areaManager.enable()
        userInterface.enable()
    }

    override fun onDisable() {
        areaManager.disable()

    }
}
