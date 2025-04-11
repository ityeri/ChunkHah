package com.github.ityeri.chunkHah.utils
import org.bukkit.block.Block
import org.bukkit.entity.Entity

class HitboxUtils {
    companion object {
        // 특정 플레이어가 접촉하는 모든 블록을 반환하는 함수
        fun getContactBlocks(entity: Entity): List<Block> {
            val boundingBox = entity.boundingBox
            val blocks = mutableListOf<Block>()

            // x, y, z 좌표를 순차적으로 검사하여 해당 범위 내의 블록을 모두 찾아 반환
            for (x in Math.floor(boundingBox.minX).toInt() until Math.ceil(boundingBox.maxX).toInt()) {
                for (y in Math.floor(boundingBox.minY).toInt() until Math.ceil(boundingBox.maxY).toInt()) {
                    for (z in Math.floor(boundingBox.minZ).toInt() until Math.ceil(boundingBox.maxZ).toInt()) {
                        val block = entity.world.getBlockAt(x, y, z)
                        blocks.add(block)
                    }
                }
            }

            return blocks
        }
    }
}
