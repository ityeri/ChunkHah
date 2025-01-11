package com.github.ityeri.chunkHah.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.PaperCommandManager
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import com.github.ityeri.chunkHah.ChunkHandler
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

@CommandAlias("player-chunkinfo")
@CommandPermission("op")
class PlayerChunkInfo(val plugin: JavaPlugin, val chunkHandler: ChunkHandler) : BaseCommand() {

    fun onEnable() {
        val commandManager = PaperCommandManager(plugin)
        commandManager.registerCommand(this)
    }

    @Default
    fun onCommand(sender: Player) {
        val chunkManager = chunkHandler.getChunkManager(sender.chunk)

        if (chunkManager == null) {
            sender.sendMessage("해당 청크는 비어 있습니다")
        } else {
            sender.sendMessage("""
청크 위치: ${sender.chunk.x}, ${sender.chunk.z}
플레이어: ${chunkManager.playerName ?: "알수없음 (플레이어 이름은 플레이어가 한번이라도 서버에 접속했을때만 확인이 가능합니다)"}
플레이어 UUID: ${chunkManager.playerUUID}
            """.trimIndent())
        }
    }
}