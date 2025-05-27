package com.github.ityeri.chunkHah.userinterface

import co.aikar.commands.PaperCommandManager
import com.github.ityeri.chunkHah.core.AreaManager
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit

class UserInterface(val areaManager: AreaManager) {
    fun enable() {
        val manager = PaperCommandManager(areaManager.plugin)

        manager.registerCommand(SaveAreaCommand(areaManager))
        manager.registerCommand(LoadAreaCommand(areaManager))

        manager.registerCommand(SetAreaCommand(areaManager))

    }
}