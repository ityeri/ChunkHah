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
import org.bukkit.entity.Player



@CommandAlias("setarea")
@CommandPermission("op")
class SetAreaCommand(val areaManager: AreaManager) : BaseCommand() {
    @Default
    @CommandCompletion("@players")
    fun onCommand(sender: CommandSender, targetPlayerName: String) {
        if (sender !is Player) {
            sender.sendMessage("이 명령어는 플레이어만 사용 가능합니다")
            return
        }

        val areaX = (sender.x / areaManager.areaWidth).toInt()
        val areaZ = (sender.z / areaManager.areaDepth).toInt()

        val targetPlayer = Bukkit.getPlayer(targetPlayerName) ?: run {
            sender.sendMessage("해당 플레이어는 존재하지 않습니다")
            return
        }

        var area = areaManager.getArea(targetPlayer)

        area ?: run {
            area = Area(targetPlayer, areaManager, 0, 0)
            areaManager.addArea(area!!)
        }

        area!!.x = areaX
        area!!.z = areaZ

        sender.sendMessage("플레이어 \"$targetPlayerName\" 의 영역을 [$areaX, $areaZ] 로 배치했습니다")
    }
}