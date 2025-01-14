package com.github.ityeri.chunkHah.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.PaperCommandManager
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import com.github.ityeri.chunkHah.ChunkHandler
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class ChunkManagerInfoCommands(val plugin: JavaPlugin, val chunkHandler: ChunkHandler) {

    fun onEnable() {
        val commandManager = PaperCommandManager(plugin)
        commandManager.registerCommand(CurrentChunkManagerInfoCommand(chunkHandler))
        commandManager.registerCommand(AllChunkManagerCommand(chunkHandler))
    }

    @CommandAlias("allchunkmanager")
    @CommandPermission("op")
    class AllChunkManagerCommand(val chunkHandler: ChunkHandler) : BaseCommand() {
        @Default
        fun onCommand(sender: Player) {
            val chunkManagerSet = chunkHandler.chunkManagerSet

            if (chunkManagerSet.size == 0) {
                sender.sendMessage("청그 매니저가 없습니다")
            } else {
                sender.sendMessage("현재 ${chunkManagerSet.size}개의 청크 매니저가 있습니다")

                for (chunkManager in chunkManagerSet) {
                    sender.sendMessage(
                        """
                        청크 위치: ${sender.chunk.x}, ${sender.chunk.z}
                        플레이어: ${chunkManager.playerName ?: "알수없음 (플레이어 이름은 플레이어가 한번이라도 서버에 접속했을때만 확인이 가능합니다)"}
                        플레이어 UUID: ${chunkManager.playerUUID}""".trimIndent()
                    )
                }
            }
        }
    }

    @CommandAlias("chunkmanagerinfo")
    @CommandPermission("op")
    class CurrentChunkManagerInfoCommand(val chunkHandler: ChunkHandler) : BaseCommand() {
        @Default
        fun onCommand(sender: Player) {
            val chunkManagerSet = chunkHandler.getChunkManager(sender.chunk)

            if (chunkManagerSet.size == 0) {
                sender.sendMessage("해당 청크는 비어 있습니다")
            } else {
                sender.sendMessage("해당 청크에는 ${chunkManagerSet.size}명의 플레이어가 있습니다")

                for (chunkManager in chunkManagerSet) {
                    sender.sendMessage(
                        """
                청크 위치: ${sender.chunk.x}, ${sender.chunk.z}
                플레이어: ${chunkManager.playerName ?: "알수없음 (플레이어 이름은 플레이어가 한번이라도 서버에 접속했을때만 확인이 가능합니다)"}
                플레이어 UUID: ${chunkManager.playerUUID}""".trimIndent()
                    )
                }
            }
        }
    }


}