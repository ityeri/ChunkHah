package com.github.ityeri.chunkHah.core

import org.bukkit.entity.Player
import java.util.UUID

class Area(
    val playerUUID: UUID, val areaManager: AreaManager,
    var x: Int, var z: Int,
) {

    constructor(
        player: Player, areaManager: AreaManager,
        x: Int, z: Int,
        width: Int, depth: Int) :
            this(player.uniqueId, areaManager, x, z)
}