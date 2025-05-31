package com.github.ityeri.chunkHah.userinterface

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import com.github.ityeri.chunkHah.core.Area
import org.bukkit.command.CommandSender

@CommandAlias("bind")
@CommandPermission("op")
class BindCommand(area: Area) : BaseCommand() {

    @Default
    fun onCommand(sender: CommandSender) {

    }
}