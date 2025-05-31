package com.github.ityeri.chunkHah.core

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.World

class FirstWorldEntryHandler(val area: Area) {
    fun update() {
        val environment = area.player!!.world.environment

        when (environment) {
            World.Environment.NORMAL -> if (!area.isEnteredOver) {
                area.isEnteredOver = true
                onFirstEnterOver()
            }

            World.Environment.NETHER -> if (!area.isEnteredNether) {
                area.isEnteredNether = true
                onFirstEnterNether()
            }

            World.Environment.THE_END -> if (!area.isEnteredEnd) {
                area.isEnteredEnd = true
                onFirstEnterEnd()
            }

            else -> Bukkit.getLogger().warning(
                "\"${area.player!!.world.name}\" 월드는 지원되지 않는 유형의 월드입니다")
        }
    }

    fun onFirstEnterOver() {
        Bukkit.getServer().sendMessage(Component.text("over"))
    }

    fun onFirstEnterNether() {
        Bukkit.getServer().sendMessage(Component.text("nether"))
    }

    fun onFirstEnterEnd() {
        Bukkit.getServer().sendMessage(Component.text("end"))
    }
}