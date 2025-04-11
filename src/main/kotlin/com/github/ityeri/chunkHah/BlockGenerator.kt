package com.github.ityeri.chunkHah

import com.github.ityeri.chunkHah.utils.GaussianRandom
import org.bukkit.Material
import org.bukkit.World
import kotlin.random.Random
import kotlin.random.nextInt

// 공허 블럭 (맵 밖의 블럭) 은 기본적으로 무시함

class BlockGenerator(
    private val centerY: Int,
    private val range: Int,
    private val probability: Double,
    private val generatingBlockType: Material,
    private val world: World,
    private val minX: Int,
    private val minZ: Int,
    private val maxX: Int,
    private val maxZ: Int,
    private val blockTypesToReplace: List<Material> = listOf(),
    private val maxFindingNumber: Int = 100
) {
    fun generating() {
        if (probability < Random.nextDouble()) { return }

        for (i in 0 until maxFindingNumber) {
            val y = GaussianRandom.gaussianRandom(centerY, range)

            val x = Random.nextInt(minX until maxX)
            val z = Random.nextInt(minZ until maxZ)

            val targetBlock = world.getBlockAt(x, y!!, z)
            if (blockTypesToReplace.size == 0) {
                targetBlock.setType(generatingBlockType)
                break
            } else if (targetBlock.type == Material.VOID_AIR) {
                break
            }
            else if (targetBlock.type in blockTypesToReplace) {
                targetBlock.setType(generatingBlockType)
                break
            }
        }

    }
}