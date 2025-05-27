package com.github.ityeri.chunkHah.userinterface

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import com.github.ityeri.chunkHah.core.AreaManager
import com.github.ityeri.chunkHah.core.serializer.AreaSerializer
import kotlinx.serialization.json.Json
import org.bukkit.command.CommandSender

@CommandAlias("areas")
@CommandPermission("op")
class AreaListCommand(val areaManager: AreaManager) : BaseCommand() {

    val json = Json {
        prettyPrint = true
        prettyPrintIndent = "    "
    }

    @Default
    fun onCommand(sender: CommandSender) {
        val areas = areaManager.getAllArea()

        sender.sendMessage("총 $areas 개의 영역이 있습니다")

        for (area in areas) {
            sender.sendMessage("\n===")
            sender.sendMessage(json.encodeToString(AreaSerializer, area))
        }
    }
}