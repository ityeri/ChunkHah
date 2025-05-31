package com.github.ityeri.chunkHah.core

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.TreeType
import org.bukkit.World

class FirstWorldEntryHandler(val area: Area) {

    val treeTypes = setOf(
        TreeType.TREE,
        TreeType.BIG_TREE,
        TreeType.REDWOOD,
        TreeType.TALL_REDWOOD,
        TreeType.BIRCH,
        TreeType.JUNGLE,
        TreeType.SMALL_JUNGLE,
        TreeType.COCOA_TREE,
        TreeType.JUNGLE_BUSH,
        TreeType.SWAMP,
        TreeType.ACACIA,
        TreeType.DARK_OAK,
        TreeType.MEGA_REDWOOD,
        TreeType.MEGA_PINE,
        TreeType.TALL_BIRCH,
        TreeType.CRIMSON_FUNGUS,
        TreeType.WARPED_FUNGUS,
        TreeType.AZALEA,
        TreeType.MANGROVE,
        TreeType.CHERRY
    )

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

        val treeX = area.minX + area.width / 2
        val treeZ = area.minX + area.depth / 2
        val treeY = area.player!!.world.getHighestBlockYAt(treeX, treeZ) + 1

        area.player!!.world.generateTree(
            Location(
                area.player!!.world, treeX.toDouble(), treeY.toDouble(), treeZ.toDouble()
            ),
            treeTypes.random()
        )
    }

    fun onFirstEnterNether() {
        Bukkit.getServer().sendMessage(Component.text("nether"))
    }

    fun onFirstEnterEnd() {
        Bukkit.getServer().sendMessage(Component.text("end"))
    }
}