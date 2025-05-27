package com.github.ityeri.chunkHah.userinterface

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import com.github.ityeri.chunkHah.core.AreaManager
import org.bukkit.command.CommandSender

@CommandAlias("loadarea")
@CommandPermission("op")
class LoadAreaCommand(val areaManager: AreaManager) : BaseCommand() {

    @Default
    fun onCommand(sender: CommandSender) {
        areaManager.areaLoader.load()
        sender.sendMessage("모든 영엳 데이터를 불러왔습니다")
    }
}