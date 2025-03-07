package com.github.ityeri.chunkHah.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.PaperCommandManager
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import com.github.ityeri.chunkHah.ChunkHandler
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.FileNotFoundException

class AriaControlCommand(val plugin: JavaPlugin, val chunkHandler: ChunkHandler) {

    fun onEnable() {
        val commandManager = PaperCommandManager(plugin)
        commandManager.registerCommand(SaveAria(chunkHandler))
        commandManager.registerCommand(LoadAria(chunkHandler))
        commandManager.registerCommand(SetChunkCommand(chunkHandler))
        commandManager.registerCommand(ClearAllChunkCommand(chunkHandler))
        commandManager.registerCommand(RmChunkManagerCommand(chunkHandler))

        commandManager.commandCompletions.registerCompletion("currentChunkX") { context ->
            val player = context.player

            if (player != null) {
                return@registerCompletion listOf("${(player.x/16).toInt()}", "~")
            }
            return@registerCompletion listOf()
        }

        commandManager.commandCompletions.registerCompletion("currentChunkZ") { context ->
            val player = context.player

            if (player != null) {
                return@registerCompletion listOf("${(player.z/16).toInt()}", "~")
            }

            return@registerCompletion listOf()
        }
    }

    @CommandAlias("savearia")
    @CommandPermission("op")
    class SaveAria(val chunkHandler: ChunkHandler) : BaseCommand() {
        @Default
        fun onCommand(sender: CommandSender) {
            chunkHandler.saveAriaData()
        }
    }

    @CommandAlias("loadaria")
    @CommandPermission("op")
    class LoadAria(val chunkHandler: ChunkHandler) : BaseCommand() {
        @Default
        fun onCommand(sender: CommandSender) {
            try {
                chunkHandler.loadAriaData()
            } catch (e: FileNotFoundException) {
                sender.sendMessage("영역 데이터 파일을 찾을수 없습니다. 자동 할당을 사용합니다")
            } catch (e: ChunkHandler.WrongAriaDataException) {
                sender.sendMessage("영역 데이터 파일의 구조가 잘못됬습니다. 자동 할당을 사용합니다")
            }
        }
    }

    @CommandAlias("setchunk")
    @CommandPermission("op")
    class SetChunkCommand(val chunkHandler: ChunkHandler) : BaseCommand() {
        @Default
        @CommandCompletion("@players @currentChunkX @currentChunkZ")
        fun onCommand(sender: CommandSender, targetPlayerName: String, newChunkXstr: String, newChunkZstr: String) {
            var senderPlayer: Player? = null
            if (sender is Player) { senderPlayer = sender }

            val targetPlayer = Bukkit.getPlayer(targetPlayerName)
            if (targetPlayer == null) {
                sender.sendMessage("해당 이름을 가진 플레이어르 찾을수 없습니다")
                return
            }


            val newChunkX: Int
            val newChunkZ: Int

            if (newChunkXstr == "~") {
                newChunkX = Math.floor(senderPlayer!!.x/16).toInt()
            } else {

                try { newChunkX = newChunkXstr.toInt() }
                catch (e: NumberFormatException) {
                    sender.sendMessage("잘못된 숫자가 입력됬습다")
                    return
                }
            }

            if (newChunkZstr == "~") {
                newChunkZ = Math.floor(senderPlayer!!.z/16).toInt()
            } else {
                try { newChunkZ = newChunkZstr.toInt() }
                catch (e: NumberFormatException) {
                    sender.sendMessage("잘못된 숫자가 입력됬습다")
                    return
                }
            }

            var chunkManager = chunkHandler.getChunkManager(targetPlayer)
            if (chunkManager == null) {
                chunkManager = chunkHandler.newChunkManager(targetPlayer)
                chunkManager!!.setChunk(
                    targetPlayer.world.getChunkAt(newChunkX, newChunkZ)
                )
            } else {
                chunkManager.setChunk(
                    targetPlayer.world.getChunkAt(newChunkX, newChunkZ)
                )
            }

        }

    }

    @CommandAlias("clearallchunk")
    @CommandPermission("op")
    class ClearAllChunkCommand(val chunkHandler: ChunkHandler) : BaseCommand() {
        @Default
        fun onCommand(sender: CommandSender) {
            for (chunkManager in chunkHandler.getChunkManagers()) {
                chunkHandler.removeChunkManager(chunkManager)
            }
            chunkHandler.saveAriaData()
        }
    }

    @CommandAlias("rmchunk")
    @CommandPermission("op")
    class RmChunkManagerCommand(val chunkHandler: ChunkHandler) : BaseCommand() {
        @Default
        @CommandCompletion("@players")
        fun onCommand(sender: CommandSender, targetPlayerName: String) {
            val targetPlayer = Bukkit.getPlayer(targetPlayerName)
            if (targetPlayer == null) {
                sender.sendMessage("해당 이름을 가진 플레이어를 찾을수 없습니다")
                return
            }

            val chunkManager = chunkHandler.getChunkManager(targetPlayer)
            if (chunkManager == null) {
                sender.sendMessage("해당 플레이어에 대한 청크 매니저가 없습니다")
                return
            }

            chunkHandler.removeChunkManager(chunkManager)
        }
    }

}
