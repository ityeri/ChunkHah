// 플레이어 하나와 청크 하나를 관리하는 객체
// ChunkHandler 의 하위 객체임

package com.github.ityeri.chunkHah

import com.github.ityeri.chunkHah.utils.HitboxUtils
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.block.CreatureSpawner
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import kotlin.random.Random


// TODO 리소스 테스크 추가, 기본 나무생성 추가, runTaskTimer 에 다 쑤셔 넣지 말고 개별 메서드로 분리 ㄱ

class ChunkManager (
    val plugin: JavaPlugin,
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
    ) : this(plugin, player.uniqueId,
        overWorldChunk, netherWorldChunk, theEndChunk)


    class WrongAriaDataException(message: String) : RuntimeException(message)
    class WrongPlayerDataException(message: String) : RuntimeException(message)



    var isBind: Boolean = true;
    private val throwStrength: Double = 0.2;

    val player: Player?
        get() { return Bukkit.getPlayer(playerUUID) }

    private var cachedPlayerName: String? = null
    val playerName: String?
        get() {
            if (player != null) {
                cachedPlayerName = player!!.name
            }
            return cachedPlayerName
        }

    var isFirstEnterOverWorld = true
    var isFirstEnterNetherWorld = true
    var isFirstEnterTheEnd = true

    private val passableBlocks: List<Material> = listOf(
        Material.AIR, Material.WATER, Material.LAVA, Material.GLASS,
        Material.SCAFFOLDING
    )





    fun onEnable() {
        Bukkit.getScheduler().runTaskTimer(plugin, Runnable {
            playerPositionCheck()
            firstEnterCheck()

        }, 0L, 1L).taskId

        Bukkit.getPluginManager().registerEvents(this, plugin)
    }


    fun playerPositionCheck() {
        // 먼저 플레이어가 재접하거나 나갔을때를 대비해서 플레 객체 유효성 검사
        // 이때 player 가 null 이 아님을 확인함
        if (player == null) { return }

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

            val chunkMinX = currentCheckChunk.minX
            val chunkMinZ = currentCheckChunk.minZ
            val chunkMaxX = currentCheckChunk.maxX
            val chunkMaxZ = currentCheckChunk.maxZ


            val newLocation = player!!.location.clone()
            val newVelocity = player!!.velocity

            // 플레이어가 이동할 좌표 계산
            if (player!!.x < chunkMinX) {
                newLocation.x = chunkMinX
                newVelocity.x = throwStrength
            }
            else if (chunkMaxX < player!!.x) {
                newLocation.x = chunkMaxX
                newVelocity.x = -throwStrength
            }

            if (player!!.z < chunkMinZ) {
                newLocation.z = chunkMinZ
                newVelocity.z = throwStrength
            }
            else if (chunkMaxZ < player!!.z) {
                newLocation.z = chunkMaxZ
                newVelocity.z = -throwStrength
            }

            // 플레이어가 땅속에 쳐박히는거 방지하기 위해서
            // Y 좌표 하나씩 올리고 수시로 tp 하며 빈곳 찾음
            while (true) {
                var isFind = true

                player!!.teleport(newLocation)
                val blocks = HitboxUtils.getContactBlocks(player!!)

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

    fun firstEnterCheck() {
        // 먼저 플레이어가 재접하거나 나갔을때를 대비해서 플레 객체 유효성 검사
        // 이때 player 가 null 이 아님을 확인함
        if (player == null) { return }

        if (player!!.world.name == "world" || isFirstEnterOverWorld) {
            isFirstEnterOverWorld = false

            // 청크 중앙 나무 생성
            val generatingX = overWorldChunk.minX + 8
            val generatingZ = overWorldChunk.minZ + 8

            val treeGeneratingLocation = Location(player!!.world,
                generatingX,
                player!!.world.getHighestBlockAt(generatingX.toInt(), generatingZ.toInt())
                    .location.y + 1,
                generatingZ,
            )

            player!!.world.generateTree(treeGeneratingLocation, TreeType.TREE)

        }
        else if (player!!.world.name == "world_nether" || isFirstEnterNetherWorld) {
            isFirstEnterNetherWorld = false

            val generatingX = netherWorldChunk.minX.toInt() + 8
            val generatingY = Random.nextInt(8, 120)
            val generatingZ = netherWorldChunk.minZ.toInt() + 8

            val generatingBlock = player!!.world.getBlockAt(
                generatingX, generatingY, generatingZ
            )

            generatingBlock.setType(Material.SPAWNER)
            val state = generatingBlock.state as CreatureSpawner

            // 1분에서 10분에 한번씩 생성됨
            state.spawnedType = EntityType.BLAZE
            state.minSpawnDelay = 20 * 60 * 1
            state.minSpawnDelay = 20 * 60 * 10

        }
        else if (player!!.world.name == "world_the_end" || isFirstEnterTheEnd) {
            isFirstEnterTheEnd = false
            player!!.sendMessage("날파리 월드에 처음 왔구나! 이건 월드 첨 들어가면 처리하는 코드의 테스트 메세지임")

        }
    }



    companion object {
        fun fromJsonObject(jsonObject: JsonObject, plugin: JavaPlugin, playerUUID: UUID) : ChunkManager {
            /*
            chunkManagerData 구조:
            {
                "world": {"x": 0, "z": 1},
                "world_nether": {"x": 2, "z": 3},
                "world_the_end": {"x": 4, "z": 5}
                "isFirst": true / false // 이 줄은 없을수도 있으므
            }
             */

            val gson = Gson()

            val overWorldChunk: Chunk
            val netherWorldChunk: Chunk
            val theEndChunk: Chunk

            val isFirstEnterOverWorld: Boolean
            val isFirstEnterNetherWorld: Boolean
            val isFirstEnterTheEnd: Boolean

            try {
                // 오버월드
                val overWorldData = gson.fromJson(jsonObject.get("world"), Map::class.java)
                        as Map<String, *>
                overWorldChunk = Bukkit.getWorld("world")!!
                    .getChunkAt(overWorldData.get("x")!! as Int, overWorldData.get("z")!! as Int)
                isFirstEnterOverWorld = overWorldData.get("isFirst") as Boolean? ?: true

                // 네더월드
                val netherWorldData = gson.fromJson(jsonObject.get("world_nether"), Map::class.java)
                        as Map<String, *>
                netherWorldChunk = Bukkit.getWorld("world_nether")!!
                    .getChunkAt(netherWorldData.get("x")!! as Int, netherWorldData.get("z")!! as Int)
                isFirstEnterNetherWorld = netherWorldData.get("isFirst") as Boolean? ?: true

                // 날파리월드
                val theEndData = gson.fromJson(jsonObject.get("world_the_end"), Map::class.java)
                        as Map<String, *>
                theEndChunk = Bukkit.getWorld("world_the_end")!!
                    .getChunkAt(netherWorldData.get("x")!! as Int, netherWorldData.get("z")!! as Int)
                isFirstEnterTheEnd = theEndData.get("isFirst") as Boolean? ?: true
            }
            catch (e: java.lang.NullPointerException) {
                throw WrongPlayerDataException("사용자 UUID: \"${playerUUID}\" 의 청크 데이터가 잘못되었습니다")
            }

            val chunkManager = ChunkManager(
                plugin, playerUUID,
                overWorldChunk, netherWorldChunk, theEndChunk
            )

            chunkManager.isFirstEnterOverWorld = isFirstEnterOverWorld
            chunkManager.isFirstEnterNetherWorld = isFirstEnterNetherWorld
            chunkManager.isFirstEnterTheEnd = isFirstEnterTheEnd

            return chunkManager
        }
    }

    fun toJsonObject(): JsonObject {
        val jsonObject = JsonObject()
        /*
        chunkManagerData 구조:
        {
            "world": {"x": 0, "z": 1},
            "world_nether": {"x": 2, "z": 3},
            "world_the_end": {"x": 4, "z": 5},
            "isFirst": true / false // 이 줄은 없을수도 있으므
        }
         */

        val overWorldChunkData = JsonObject()
        overWorldChunkData.addProperty("x", overWorldChunk.x)
        overWorldChunkData.addProperty("z", overWorldChunk.z)
        overWorldChunkData.addProperty("isFirst", isFirstEnterOverWorld)

        val netherWorldChunkData = JsonObject()
        netherWorldChunkData.addProperty("x", netherWorldChunk.x)
        netherWorldChunkData.addProperty("z", netherWorldChunk.z)
        netherWorldChunkData.addProperty("isFirst", isFirstEnterNetherWorld)

        val theEndChunkData = JsonObject()
        theEndChunkData.addProperty("x", theEndChunk.x)
        theEndChunkData.addProperty("z", theEndChunk.z)
        theEndChunkData.addProperty("isFirst", isFirstEnterTheEnd)

        jsonObject.add("world", overWorldChunkData)
        jsonObject.add("world_nether", netherWorldChunkData)
        jsonObject.add("world_the_end", theEndChunkData)

        return jsonObject
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

val Chunk.minX: Double get() = this.x * 16.0
val Chunk.maxX: Double get() = this.x * 16.0 + 16

val Chunk.minZ: Double get() = this.z * 16.0
val Chunk.maxZ: Double get() = this.z * 16.0 + 16
