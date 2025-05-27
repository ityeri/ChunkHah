package com.github.ityeri.chunkHah.userinterface

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import com.github.ityeri.chunkHah.core.AreaManager
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

@CommandAlias("areas")
@CommandPermission("op")
class AreaListCommand(val areaManager: AreaManager) : BaseCommand() {

    @Default
    fun onCommand(sender: CommandSender) {
        val areas = areaManager.getAllArea()

        sender.sendMessage("총 ${areas.size}개의 영역이 있습니다")

        for (area in areas) {
            sender.sendMessage(" |  ")
            sender.sendMessage(" |  플레이어: ${Bukkit.getOfflinePlayer(area.playerUUID).name}")
            sender.sendMessage(" |  위치: [${area.x}, ${area.z}]")
            sender.sendMessage(" |  제약 활성화 여부: ${area.isEnabled}")
        }
    }
}