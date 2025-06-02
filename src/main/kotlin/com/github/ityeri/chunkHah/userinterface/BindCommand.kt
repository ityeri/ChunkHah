package com.github.ityeri.chunkHah.userinterface

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import com.github.ityeri.chunkHah.core.Area
import com.github.ityeri.chunkHah.core.AreaManager
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

@CommandAlias("bind")
@CommandPermission("op")
class BindCommand(val areaManager: AreaManager) : BaseCommand() {

    @Default
    @CommandCompletion("@players")
    fun onCommand(sender: CommandSender, targetPlayerName: String) {
        val area = areaManager.getArea(
            Bukkit.getOfflinePlayer(targetPlayerName)
        ) ?: run {
            sender.sendMessage("해당 플레이어는 아직 영역을 할당받지 않았습니다. 먼저 /setarea 를 해주세요")
            return
        }

        area.isBind = true
        sender.sendMessage("$targetPlayerName 플레이어의 영역 제약을 활성화 했습니다")
    }
}