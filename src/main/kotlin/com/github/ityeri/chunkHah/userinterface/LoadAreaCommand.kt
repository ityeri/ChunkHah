package com.github.ityeri.chunkHah.userinterface

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import com.github.ityeri.chunkHah.core.AreaManager
import org.bukkit.command.CommandSender
import java.io.FileNotFoundException

@CommandAlias("loadarea")
@CommandPermission("op")
class LoadAreaCommand(val areaManager: AreaManager) : BaseCommand() {

    @Default
    @CommandCompletion("@nothing")
    fun onCommand(sender: CommandSender) {
        try {
            areaManager.areaLoader.load()
        } catch (_: FileNotFoundException) {
            sender.sendMessage("영역파일(plugins/ChunkHah/area_data.json) 을 찾을수 없습니다")
            return
        }
        sender.sendMessage("모든 영역 데이터를 불러왔습니다")
    }
}