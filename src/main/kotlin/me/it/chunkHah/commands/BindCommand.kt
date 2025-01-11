package me.it.chunkHah.commands

import co.aikar.commands.BaseCommand
import org.bukkit.plugin.java.JavaPlugin
import co.aikar.commands.PaperCommandManager
import co.aikar.commands.annotation.*
import me.it.chunkHah.ChunkHandler
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender


class BindCommand(val plugin: JavaPlugin, val chunkHandler: ChunkHandler) {
    val manager = PaperCommandManager(plugin)

    fun onEnable() {
        val commandManager = PaperCommandManager(plugin)
        commandManager.registerCommand(Bind(chunkHandler))
        commandManager.registerCommand(Unbind(chunkHandler))
    }

    @CommandAlias("bind")
    @CommandPermission("op")
    class Bind(val chunkHandler: ChunkHandler) : BaseCommand() {
        @Default
        @CommandCompletion("@players @nothing")
        fun onCommand(sender: CommandSender, targetPlayerName: String) {
            val targetPlayer = Bukkit.getPlayer(targetPlayerName)

            if (targetPlayer == null) {
                sender.sendMessage("플레이어 $targetPlayerName 을/를 찾을수 없습니다")
                return
            } else {
                val chunkManager = chunkHandler.getChunkManager(targetPlayer)

                if (chunkManager == null) {
                    sender.sendMessage("플레이어 $targetPlayerName 에 대한 청크 매니저가 없습니다")
                } else if (chunkManager.isBind == true) {
                    sender.sendMessage("플레이어 $targetPlayerName 은/는 이미 바인드 되어 있습니다")
                } else {
                    chunkManager.bind()
                    sender.sendMessage("플레이어 $targetPlayerName 을/를 성공적으로 바인드 했습니다")
                }
            }
        }
    }

    @CommandAlias("unbind")
    @CommandPermission("op")
    class Unbind(val chunkHandler: ChunkHandler) : BaseCommand() {
        @Default
        @CommandCompletion("@players @nothing")
        fun onCommand(sender: CommandSender, targetPlayerName: String) {
            val targetPlayer = Bukkit.getPlayer(targetPlayerName)

            if (targetPlayer == null) {
                sender.sendMessage("플레이어 $targetPlayerName 을/를 찾을수 없습니다")
                return
            } else {
                val chunkManager = chunkHandler.getChunkManager(targetPlayer)

                if (chunkManager == null) {
                    sender.sendMessage("플레이어 $targetPlayerName 에 대한 청크 매니저가 없습니다")
                } else if (chunkManager.isBind == false) {
                    sender.sendMessage("플레이어 $targetPlayerName 은/는 이미 바인드 해제 되어 있습니다")
                } else {
                    chunkManager.unbind()
                    sender.sendMessage("플레이어 $targetPlayerName 을/를 성공적으로 바인드 해제 했습니다")
                }
            }
        }
    }

}