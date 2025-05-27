package com.github.ityeri.chunkHah.userinterface

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import com.github.ityeri.chunkHah.core.AreaManager
import org.bukkit.command.CommandSender

@CommandAlias("savearea")
@CommandPermission("op")
class SaveAreaCommand(val areaManager: AreaManager) : BaseCommand() {

    @Default
    fun onCommand(sender: CommandSender) {
        areaManager.areaLoader.save()
        sender.sendMessage("모든 영역 데이터를 저장했습니다")
    }
}
