package com.github.ityeri.chunkHah.userinterface.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import com.github.ityeri.chunkHah.core.AreaManager
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandAlias("bind")
@CommandPermission("op")
class BindCommand(val areaManager: AreaManager) : BaseCommand() {

    @Default
    @CommandCompletion("@nothing")
    fun onCommand(sender: CommandSender) {

        if (sender !is Player) {
            sender.sendMessage("이 명령어는 플레이어만 사용 가능합니다")
            return
        }

        val area = areaManager.getArea(sender) ?: run {
            sender.sendMessage("해당 플레이어는 아직 영역을 할당받지 않았습니다. 먼저 /setarea 를 해주세요")
            return
        }

        area.isBind = true
        sender.sendMessage("${sender.name} 플레이어의 영역 제약을 활성화 했습니다")
    }

    @Default
    @CommandCompletion("@players @nothing")
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

    @Default
    @CommandCompletion("0 0 @nothing")
    fun onCommand(sender: CommandSender, x: Int, z: Int) {
        val area = areaManager.getArea(x, z) ?:
        run {
            sender.sendMessage("해당 위치에 할당된 영역이 없습니다")
            return
        }

        area.isBind = true
        sender.sendMessage("[$x, $z] 위치의 영역 제약을 활성화 했습니다")
    }
}