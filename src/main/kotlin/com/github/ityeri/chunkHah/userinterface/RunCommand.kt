package com.github.ityeri.chunkHah.userinterface

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import com.github.ityeri.chunkHah.core.AreaManager
import org.bukkit.command.CommandSender
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory
import javax.script.ScriptException

@CommandAlias("run")
@CommandPermission("op")
class RunCommand(val areaManager: AreaManager) : BaseCommand() {

    val engine = NashornScriptEngineFactory().scriptEngine

    init {
        engine.put("manager", areaManager)
    }

    @Default
    fun onCommand(sender: CommandSender, code: String) {

        try {
            val result = engine.eval(code)
            sender.sendMessage(result.toString())
        } catch (e: ScriptException) {
            sender.sendMessage("코드를 실행하던중 에러가 발생했습니다! :")
            sender.sendMessage(e.toString())
        }
    }
}