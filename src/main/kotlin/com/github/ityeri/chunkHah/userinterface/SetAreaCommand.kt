package com.github.ityeri.chunkHah.userinterface

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import com.github.ityeri.chunkHah.core.AreaManager
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/*
@CommandAlias("setchunk")
@CommandPermission("op")
class SetChunkCommand(val chunkHandler: ChunkHandler) : BaseCommand() {
    @Default
    @CommandCompletion("@players @currentChunkX @currentChunkZ")
    fun onCommand(sender: CommandSender, targetPlayerName: String, newChunkXstr: String, newChunkZstr: String) {

 */

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

        val targetPlayer = Bukkit.getPlayer(targetPlayerName)!!
    }
}