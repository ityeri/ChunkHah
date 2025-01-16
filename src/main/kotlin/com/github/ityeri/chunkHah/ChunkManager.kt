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
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.Tag
import java.util.*
import kotlin.random.Random


// 리소스 테스크 추가

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
    private val throwStrength: Double = 0.5;
    private val blank: Double = 0.01;

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
        Material.SCAFFOLDING, Material.CHEST,
    )

    private val passbleTags: List<Tag<Material>> = listOf(
        Tag.SLABS,
        Tag.FENCES,
        Tag.WALLS,
        Tag.LEAVES,
        Tag.DOORS
    )





    fun onEnable() {
        Bukkit.getPluginManager().registerEvents(this, plugin)
    }

    fun onDisable() {
        HandlerList.unregisterAll(this)
    }


    fun update() {
        playerPositionCheck()
        firstEnterCheck()
        cachePlayerName()

        overWorldMineralGenerator()
        netherWorldMineralGenerator()
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
                newLocation.x = chunkMinX + blank
                newVelocity.x = throwStrength
            }
            else if (chunkMaxX < player!!.x) {
                newLocation.x = chunkMaxX - blank
                newVelocity.x = -throwStrength
            }

            if (player!!.z < chunkMinZ) {
                newLocation.z = chunkMinZ + blank
                newVelocity.z = throwStrength
            }
            else if (chunkMaxZ < player!!.z) {
                newLocation.z = chunkMaxZ - blank
                newVelocity.z = -throwStrength
            }

            // 플레이어가 땅속에 쳐박히는거 방지하기 위해서
            // Y 좌표 하나씩 올리고 수시로 tp 하며 빈곳 찾음
            while (true) {

                player!!.teleport(newLocation)
                val blocks = HitboxUtils.getContactBlocks(player!!)

                var isBlockPassable = false

                for (block in blocks) {

                    if (block.type in passableBlocks || block.isPassable) {
                        // 블럭이 통과 가능한 블럭에 포함도 있거나 통과 가능할경우
                        isBlockPassable = true
                    } else {
                        isBlockPassable = false
                    }

                    if (!isBlockPassable) {
                        for (tag in passbleTags) {
                            if (tag.isTagged(block.type)) {
                                isBlockPassable = true
                                break
                            }
                        }
                    }

                }


                if (isBlockPassable) {
                    break
                } else {
                    newLocation.y += 1
                }
            }

            player!!.teleport(newLocation)
            player!!.velocity = newVelocity
        }
    }

    fun firstEnterCheck() {
        // 먼저 플레이어가 재접하거나 나갔을때를 대비해서 플레 객체 유효성 검사
        // 이때 player 가 null 이 아님을 확인함
        if (player == null) { return }

        if (player!!.world.name == "world" && isFirstEnterOverWorld) {
            isFirstEnterOverWorld = false
            onOverWorldFirstEnter()
        }
        else if (player!!.world.name == "world_nether" && isFirstEnterNetherWorld) {
            isFirstEnterNetherWorld = false
            onNetherWorldFirstEnter()
        }
        else if (player!!.world.name == "world_the_end" && isFirstEnterTheEnd) {
            isFirstEnterTheEnd = false
            onTheEndFirstEnter()
        }
    }

    fun onOverWorldFirstEnter() {
        // 청크 중앙 나무 생성
        val generatingX = overWorldChunk.minX + 8
        val generatingZ = overWorldChunk.minZ + 8

        val treeGeneratingLocation = Location(player!!.world,
            generatingX,
            player!!.world.getHighestBlockAt(generatingX.toInt(), generatingZ.toInt())
                .location.y + 1,
            generatingZ,
        )

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

        player!!.world.generateTree(treeGeneratingLocation, treeTypes.random())
    }
    fun onNetherWorldFirstEnter() {
        // 블레이즈 스포너 생성
        val generatingX = netherWorldChunk.minX.toInt() + 8
        var generatingY = Random.nextInt(8, 120)
        val generatingZ = netherWorldChunk.minZ.toInt() + 8

        val blazeSpawner = player!!.world.getBlockAt(
            generatingX, generatingY, generatingZ
        )

        blazeSpawner.setType(Material.SPAWNER)
        val blazeSpawnerState = blazeSpawner.state as CreatureSpawner

        // 1분에서 10분에 한번씩 생성됨
        blazeSpawnerState.spawnedType = EntityType.BLAZE
        blazeSpawnerState.maxSpawnDelay = 20 * 60 * 10
        blazeSpawnerState.minSpawnDelay = 20 * 60 * 1
        blazeSpawnerState.maxNearbyEntities = 6
        blazeSpawnerState.requiredPlayerRange = 16
        blazeSpawnerState.spawnCount = 1
        blazeSpawnerState.spawnRange = 4

        blazeSpawnerState.update()


        // 엔더맨 스포너도 생성
        val oldY = generatingY
        while (oldY == generatingY) { generatingY = Random.nextInt(8, 120) }

        val endermanSpawner = player!!.world.getBlockAt(
            generatingX, generatingY, generatingZ
        )

        endermanSpawner.setType(Material.SPAWNER)
        val endermanSpawnerState = endermanSpawner.state as CreatureSpawner

        // 30초에서 1분에 한번씩 생성됨
        endermanSpawnerState.spawnedType = EntityType.ENDERMAN
        endermanSpawnerState.maxSpawnDelay = 20 * 60
        endermanSpawnerState.minSpawnDelay = 20 * 30
        endermanSpawnerState.maxNearbyEntities = 6
        endermanSpawnerState.requiredPlayerRange = 16
        endermanSpawnerState.spawnCount = 1
        endermanSpawnerState.spawnRange = 4

        endermanSpawnerState.update()
    }
    fun onTheEndFirstEnter() {
        player!!.sendMessage("날파리 월드에 처음 왔구나! 이건 월드 첨 들어가면 처리하는 코드rjtltl 테스트 메세지임")
    }

    fun cachePlayerName() { playerName }


    fun overWorldMineralGenerator() {
        // TODO
    }

    fun netherWorldMineralGenerator() {
        // TODO
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
                    .getChunkAt(
                        (overWorldData.get("x")!! as Double).toInt(),
                        (overWorldData.get("z")!! as Double).toInt()
                    )
                isFirstEnterOverWorld = overWorldData.get("isFirst") as Boolean? ?: true

                // 네더월드
                val netherWorldData = gson.fromJson(jsonObject.get("world_nether"), Map::class.java)
                        as Map<String, *>
                netherWorldChunk = Bukkit.getWorld("world_nether")!!
                    .getChunkAt(
                        (netherWorldData.get("x")!! as Double).toInt(),
                        (netherWorldData.get("z")!! as Double).toInt()
                    )
                isFirstEnterNetherWorld = netherWorldData.get("isFirst") as Boolean? ?: true

                // 날파리월드
                val theEndData = gson.fromJson(jsonObject.get("world_the_end"), Map::class.java)
                        as Map<String, *>
                theEndChunk = Bukkit.getWorld("world_the_end")!!
                    .getChunkAt(
                        (theEndData.get("x")!! as Double).toInt(),
                        (theEndData.get("z")!! as Double).toInt()
                    )
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

    fun setChunk(chunk: Chunk) {
        if (chunk.world.name == "world") { overWorldChunk = chunk }
        if (chunk.world.name == "world_nether") { netherWorldChunk = chunk }
        if (chunk.world.name == "world_the_end") { theEndChunk = chunk }
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

    val Chunk.minX: Double get() = this.x * 16.0
    val Chunk.maxX: Double get() = this.x * 16.0 + 16

    val Chunk.minZ: Double get() = this.z * 16.0
    val Chunk.maxZ: Double get() = this.z * 16.0 + 16
}
