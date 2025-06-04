package com.github.ityeri.chunkHah.userinterface.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import com.github.ityeri.chunkHah.core.Area
import com.github.ityeri.chunkHah.core.AreaManager
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

// 묵시 (자기자신)
// 타 플레이어
// 위치

@CommandAlias("rmarea")
@CommandPermission("op")
class RmAreaCommand(val areaManager: AreaManager) : BaseCommand() {

    @Default
    @CommandPermission("@nothing")
    fun onCommand(sender: CommandSender) {
        if (sender !is Player) {
            sender.sendMessage("이 명령어는 플레이어만 사용 가능합니다")
            return
        }

        if (areaManager.rmArea(sender) is Area) {
            sender.sendMessage("플레이어 \"${sender.name}\" 의 영역이 성공적으로 제거되었습니다")
        } else {
            sender.sendMessage("해당 플레이어에게 할당된 영역이 없습니다")
        }
    }

}