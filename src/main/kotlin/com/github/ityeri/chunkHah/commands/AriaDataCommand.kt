package com.github.ityeri.chunkHah.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.PaperCommandManager
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Default
import com.github.ityeri.chunkHah.ChunkHandler
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import java.io.FileNotFoundException

class AriaDataCommand(val plugin: JavaPlugin, val chunkHandler: ChunkHandler) {

    fun onEnable() {
        val commandManager = PaperCommandManager(plugin)
        commandManager.registerCommand(SaveAria(chunkHandler))
        commandManager.registerCommand(LoadAria(chunkHandler))
    }

    @CommandAlias("savearia")
    class SaveAria(val chunkHandler: ChunkHandler) : BaseCommand() {
        @Default
        fun onCommand(sender: CommandSender) {
            chunkHandler.saveAriaData()
        }
    }

    @CommandAlias("loadaria")
    class LoadAria(val chunkHandler: ChunkHandler) : BaseCommand() {
        @Default
        fun onCommand(sender: CommandSender) {
            try {
                chunkHandler.loadAriaDatas()
            } catch (e: FileNotFoundException) {
                sender.sendMessage("영역 데이터 파일을 찾을수 없습니다. 자동 할당을 사용합니다")
            } catch (e: ChunkHandler.WrongAriaDataException) {
                sender.sendMessage("영역 데이터 파일의 구조가 잘못됬습니다. 자동 할당을 사용합니다")
            }
        }
    }
}