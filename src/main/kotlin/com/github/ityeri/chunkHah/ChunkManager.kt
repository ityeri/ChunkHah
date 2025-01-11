// 플레이어 하나와 청크 하나를 관리하는 객체
// ChunkHandler 의 하위 객체임

package com.github.ityeri.chunkHah

import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.plugin.java.JavaPlugin
import java.util.UUID


// TODO 리소스 테스크 추가, 기본 나무생성 추가, runTaskTimer 에 다 쑤셔 넣지 말고 개별 메서드로 분리 ㄱ

class ChunkManager (
    val plugin: JavaPlugin,
    var player: Player?,
    val playerUUID: UUID,
    var overWorldChunk: Chunk,
    var netherWorldChunk: Chunk,
    var theEndChunk: Chunk
) : Listener {

    constructor(
        plugin: JavaPlugin,
        player: Player,
        overWorldChunk: Chunk,
        netherWorldChunk: Chunk,
        theEndChunk: Chunk
    ) : this(plugin, player, player.uniqueId,
        overWorldChunk, netherWorldChunk, theEndChunk)

    constructor(
        plugin: JavaPlugin,
        playerUUID: UUID,
        overWorldChunk: Chunk,
        netherWorldChunk: Chunk,
        theEndChunk: Chunk
    ) : this(plugin, null, playerUUID,
        overWorldChunk, netherWorldChunk, theEndChunk)


    var isBind: Boolean = true;
    private val throwStrength: Double = 0.2;
    private val hitboxUtils: HitboxUtils = HitboxUtils()

    private var cachedPlayerName: String? = null
    val playerName: String?
        get() {
            if (player != null) {
                cachedPlayerName = player!!.name
            }
            return cachedPlayerName
        }

    val isFirstEnterOverWorld = true
    val isFirstEnterNetherWorld = true
    val isFirstEnterTheEnd = true

    private val passableBlocks: List<Material> = listOf(
        Material.AIR, Material.WATER, Material.LAVA, Material.GLASS,
        Material.SCAFFOLDING
    )



    fun onEnable() {
        Bukkit.getScheduler().runTaskTimer(plugin, Runnable {
            playerPosithionCheck()

        }, 0L, 1L).taskId


        Bukkit.getPluginManager().registerEvents(this, plugin)
    }


    fun playerPosithionCheck() {
        // 플레이어가 재접하거나 나갔을때를 대비해서 플레 객체 유효성 검사
        // 이때 player 가 null 이 아님을 확인함
        if (player == null || !player!!.isConnected) {
            player = Bukkit.getPlayer(playerUUID) ?: return
        }

        // 청크 이동 제한이 무시되는 경우를 처리함
        if (!isBind) { return }
        if (player!!.isDead) { return }

        // 어디 타고 있으면
        if (player!!.isInsideVehicle) { return }



        // 플레이어가 위치하는 월드 기준으로 체크할 청크 가져오기
        val currentCheckChunk: Chunk;
        if (player!!.world.name == "world") { currentCheckChunk = overWorldChunk }
        else if (player!!.world.name == "world_nether") { currentCheckChunk = netherWorldChunk }
        else if (player!!.world.name == "world_the_end") { currentCheckChunk = theEndChunk }

        else { throw Error("플레이어가 위치한 월드 \"${player!!.world.name}\" 은/는 지원되지 않습니다") }


        // 청크 밖에 있을경우
        if (!isInChunk(player!!.location)) {

            val chunkMinX = currentCheckChunk.x * 16
            val chunkMinZ = currentCheckChunk.z * 16
            val chunkMaxX = chunkMinX + 16
            val chunkMaxZ = chunkMinZ + 16


            val newLocation = player!!.location.clone()
            val newVelocity = player!!.velocity

            // 플레이어가 이동할 좌표 계산
            if (player!!.x < chunkMinX) {
                newLocation.x = chunkMinX.toDouble()
                newVelocity.x = throwStrength
            }
            else if (chunkMaxX < player!!.x) {
                newLocation.x = chunkMaxX.toDouble()
                newVelocity.x = -throwStrength
            }

            if (player!!.z < chunkMinZ) {
                newLocation.z = chunkMinZ.toDouble()
                newVelocity.z = throwStrength
            }
            else if (chunkMaxZ < player!!.z) {
                newLocation.z = chunkMaxZ.toDouble()
                newVelocity.z = -throwStrength
            }

            // 플레이어가 땅속에 쳐박히는거 방지하기 위해서
            // Y 좌표 하나씩 올리고 수시로 tp 하며 빈곳 찾음
            while (true) {
                var isFind = true

                player!!.teleport(newLocation)
                val blocks = hitboxUtils.getContactBlocks(player!!)

                for (block in blocks) {
                    if (block.type in passableBlocks || block.isPassable) {
                        // 블럭이 통과 가능한 블럭에 포함도 있거나 통과 가능할경우
                    } else {
                        newLocation.y += 1
                        isFind = false
                    }
                }

                if (isFind) { break }
            }

            player!!.teleport(newLocation)
            player!!.velocity = newVelocity
        }
    }


    fun bind() { isBind = true }
    fun unbind() { isBind = false }



    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if (event.player != player) { return }
        if (!isBind) { return }
        val clickedBlock: Block? = event.clickedBlock

        when (event.action) {
            Action.RIGHT_CLICK_BLOCK -> {
                if (!isInChunk(event.clickedBlock!!.location)) { event.isCancelled = true }
            }
            Action.LEFT_CLICK_BLOCK -> {
                if (!isInChunk(event.clickedBlock!!.location)) { event.isCancelled = true }
            }
            Action.RIGHT_CLICK_AIR -> {
                //
            }
            Action.LEFT_CLICK_AIR -> {
                //
            }
            Action.PHYSICAL -> {
                if (!isInChunk(event.clickedBlock!!.location)) { event.isCancelled = true }
            }
            else -> {
                if (!isInChunk(event.clickedBlock!!.location)) { event.isCancelled = true }
            }
        }

    }

    @EventHandler
    fun onPlayerInteractEntity(event: PlayerInteractEntityEvent) {
        if (event.player != player) { return }
        if (!isBind) { return }
        if (!isInChunk(event.rightClicked.location)) { event.isCancelled = true }
    }

    fun isInChunk(location: Location): Boolean {
        // 플레이어가 위치하는 월드 기준으로 체크할 청크 가져오기
        val currentCheckChunk: Chunk;
        if (location.world.name == "world") { currentCheckChunk = overWorldChunk }
        else if (location.world.name == "world_nether") { currentCheckChunk = netherWorldChunk }
        else if (location.world.name == "world_the_end") { currentCheckChunk = theEndChunk }

        else { throw Error("\"${playerName}\" 플레이어가 위치한 월드 \"${location.world.name}\" 은/는 지원되지 않습니다") }

        // 청크 밖에 있을경우
        if (currentCheckChunk != location.chunk) {
            return false
        }

        return true
    }

}