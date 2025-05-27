package com.github.ityeri.chunkHah.userinterface

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import com.github.ityeri.chunkHah.core.AreaManager
import org.bukkit.command.CommandSender
import javax.script.ScriptEngineManager

@CommandAlias("run")
@CommandPermission("op")
class RunCommand(val areaManager: AreaManager) : BaseCommand() {

    val engine = ScriptEngineManager().getEngineByName("JavaScript")

    init {
        engine.put("manager", areaManager)
    }

    @Default
    fun onCommand(sender: CommandSender, code: String) {

        val result = engine.eval(code)
        sender.sendMessage(result.toString())
    }
}