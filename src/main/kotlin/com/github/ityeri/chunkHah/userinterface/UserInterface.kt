package com.github.ityeri.chunkHah.userinterface

import co.aikar.commands.PaperCommandManager
import com.github.ityeri.chunkHah.core.AreaManager
import com.github.ityeri.chunkHah.userinterface.command.AreaListCommand
import com.github.ityeri.chunkHah.userinterface.command.BindCommand
import com.github.ityeri.chunkHah.userinterface.command.LoadAreaCommand
import com.github.ityeri.chunkHah.userinterface.command.RmAreaCommand
import com.github.ityeri.chunkHah.userinterface.command.RunCommand
import com.github.ityeri.chunkHah.userinterface.command.SaveAreaCommand
import com.github.ityeri.chunkHah.userinterface.command.SetAreaCommand
import com.github.ityeri.chunkHah.userinterface.command.UnbindCommand

class UserInterface(val areaManager: AreaManager) {
    fun enable() {
        val commandManager = PaperCommandManager(areaManager.plugin)

        commandManager.registerCommand(SaveAreaCommand(areaManager))
        commandManager.registerCommand(LoadAreaCommand(areaManager))
        commandManager.registerCommand(AreaListCommand(areaManager))

        commandManager.registerCommand(SetAreaCommand(areaManager))
        commandManager.registerCommand(RmAreaCommand(areaManager))

        commandManager.registerCommand(BindCommand(areaManager))
        commandManager.registerCommand(UnbindCommand(areaManager))

        commandManager.registerCommand(RunCommand(areaManager))


    }
}