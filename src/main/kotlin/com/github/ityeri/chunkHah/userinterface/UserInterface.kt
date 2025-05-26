package com.github.ityeri.chunkHah.userinterface

import co.aikar.commands.PaperCommandManager
import com.github.ityeri.chunkHah.core.AreaManager

class UserInterface(val areaManager: AreaManager) {
    fun enable() {
        val manager = PaperCommandManager(areaManager.plugin)

        manager.registerCommand(SetAreaCommand(areaManager))

    }
}