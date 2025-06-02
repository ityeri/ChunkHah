package com.github.ityeri.chunkHah.userinterface

import co.aikar.commands.PaperCommandManager
import com.github.ityeri.chunkHah.core.AreaManager

class UserInterface(val areaManager: AreaManager) {
    fun enable() {
        val commandManager = PaperCommandManager(areaManager.plugin)

        commandManager.registerCommand(SaveAreaCommand(areaManager))
        commandManager.registerCommand(LoadAreaCommand(areaManager))
        commandManager.registerCommand(AreaListCommand(areaManager))
        commandManager.registerCommand(SetAreaCommand(areaManager))

        commandManager.registerCommand(BindCommand(areaManager))
        commandManager.registerCommand(UnbindCommand(areaManager))

        commandManager.registerCommand(RunCommand(areaManager))


    }
}