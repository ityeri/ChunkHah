package com.github.ityeri.chunkHah

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.plugin.java.JavaPlugin
import kotlin.random.Random

class EndPortalFrameDropper(val plugin: JavaPlugin) : Listener {

    fun onEnable() {
        Bukkit.getPluginManager().registerEvents(this, plugin)
    }

    @EventHandler
    fun onEndermanDeath(event: EntityDeathEvent) {
        val entity = event.entity

        // 엔티티가 엔더맨인지 확인
        if (entity.type == org.bukkit.entity.EntityType.ENDERMAN) {
            val dropChance = 0.07 // 7퍼로 프레임 드랍함

            if (Random.nextDouble() < dropChance) {
                entity.world.dropItemNaturally(
                    entity.location, org.bukkit.inventory.ItemStack(Material.END_PORTAL_FRAME, 1))
            }
        }
    }

    @EventHandler
    fun onEndermanSpawn(event: CreatureSpawnEvent) {
        // 자연 생성된 모든 엔더맨 삭제
        // 스포너 스폰은 안지움
        val entity = event.entity

        if (entity.type == org.bukkit.entity.EntityType.ENDERMAN) {
            if (event.spawnReason == CreatureSpawnEvent.SpawnReason.NATURAL) {
                event.isCancelled = true
            }
        }
    }

}