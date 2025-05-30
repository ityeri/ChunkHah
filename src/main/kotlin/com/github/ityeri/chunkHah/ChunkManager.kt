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
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.inventory.ItemStack
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
    private val throwStrength: Double = 0.1;
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

    val golbalProbability = 0.002


    val blockTypesToReplace: List<Material> = listOf(
        Material.STONE, Material.COBBLESTONE,
        Material.DEEPSLATE, Material.COBBLED_DEEPSLATE,
//        Material.LAVA, Material.WATER
    )

//    val blockTypesToReplaceLiquid: List<Material> = listOf(
//        Material.STONE, Material.COBBLESTONE,
//        Material.DEEPSLATE, Material.COBBLED_DEEPSLATE
//    )


    val overWorldBlockGenerators: List<BlockGenerator> = listOf(
        BlockGenerator(16, 40, 1 * golbalProbability,
            Material.DEEPSLATE_IRON_ORE, Bukkit.getWorld("world")!!,
            overWorldChunk.minX, overWorldChunk.minZ,
            overWorldChunk.maxX, overWorldChunk.maxZ,
            blockTypesToReplace, 1),

        BlockGenerator(-64, 80, 0.3 * golbalProbability,
            Material.DEEPSLATE_DIAMOND_ORE, Bukkit.getWorld("world")!!,
            overWorldChunk.minX, overWorldChunk.minZ,
            overWorldChunk.maxX, overWorldChunk.maxZ,
            blockTypesToReplace, 1),

        BlockGenerator(0, 32, 0.15 * golbalProbability,
            Material.DEEPSLATE_LAPIS_ORE, Bukkit.getWorld("world")!!,
            overWorldChunk.minX, overWorldChunk.minZ,
            overWorldChunk.maxX, overWorldChunk.maxZ,
            blockTypesToReplace, 1),

        BlockGenerator(96, 96, 1.7 * golbalProbability,
            Material.COAL_ORE, Bukkit.getWorld("world")!!,
            overWorldChunk.minX, overWorldChunk.minZ,
            overWorldChunk.maxX, overWorldChunk.maxZ,
            blockTypesToReplace, 1),

        BlockGenerator(-64, 32, 1 * golbalProbability,
            Material.DEEPSLATE_REDSTONE_ORE, Bukkit.getWorld("world")!!,
            overWorldChunk.minX, overWorldChunk.minZ,
            overWorldChunk.maxX, overWorldChunk.maxZ,
            blockTypesToReplace, 1),

        BlockGenerator(-16, 48, 0.3 * golbalProbability,
            Material.DEEPSLATE_GOLD_ORE, Bukkit.getWorld("world")!!,
            overWorldChunk.minX, overWorldChunk.minZ,
            overWorldChunk.maxX, overWorldChunk.maxZ,
            blockTypesToReplace, 1),

        BlockGenerator(48, 64, 1 * golbalProbability,
            Material.COPPER_ORE, Bukkit.getWorld("world")!!,
            overWorldChunk.minX, overWorldChunk.minZ,
            overWorldChunk.maxX, overWorldChunk.maxZ,
            blockTypesToReplace, 1),



        BlockGenerator(32, 20, 0.01 * golbalProbability,
            Material.WATER, Bukkit.getWorld("world")!!,
            overWorldChunk.minX, overWorldChunk.minZ,
            overWorldChunk.maxX, overWorldChunk.maxZ,
            blockTypesToReplace, 1),

        BlockGenerator(-64, 10, 0.02 * golbalProbability,
            Material.LAVA, Bukkit.getWorld("world")!!,
            overWorldChunk.minX, overWorldChunk.minZ,
            overWorldChunk.maxX, overWorldChunk.maxZ,
            blockTypesToReplace, 1),
    )





    fun onEnable() {
        Bukkit.getPluginManager().registerEvents(this, plugin)
    }

    fun onDisable() {
        HandlerList.unregisterAll(this)
    }


    fun update() {
        if (player == null) { return }
        playerPositionCheck()
        firstEnterCheck()
        cachingPlayerName()

        overWorldMineralGenerator()
        netherWorldMineralGenerator()
    }


    fun playerPositionCheck() {
        // 이 함수는 update 함수에서 플레이어 객체가 null 이 아님을 보장하고 실행됨

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
        // 이 함수는 update 함수에서 플레이어 객체가 null 이 아님을 보장하고 실행됨

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
        val generatingX = overWorldChunk.minX + 8.0
        val generatingZ = overWorldChunk.minZ + 8.0

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

        // 플레이어 기본템 지금
        player!!.inventory.addItem(
            ItemStack(Material.OBSIDIAN, 30)
        )

        player!!.inventory.addItem(
            ItemStack(Material.FLINT_AND_STEEL, 1)
        )
    }
    fun onTheEndFirstEnter() {
        player!!.sendMessage("날파리 월드에 처음 왔구나! 이건 월드 첨 들어가면 처리하는 코드rjtltl 테스트 메세지임")
    }

    fun cachingPlayerName() { playerName }


    fun overWorldMineralGenerator() {
        for (generator in overWorldBlockGenerators) {
            generator.generating()
        }
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
//                if (!isInChunk(clickedBlock!!.x, clickedBlock.z, clickedBlock.world)) {
//                    event.isCancelled = true
//                }
            }
            Action.LEFT_CLICK_BLOCK -> {
                if (!isInChunk(clickedBlock!!.x, clickedBlock.z, clickedBlock.world)) {
                    event.isCancelled = true
                }
            }
            Action.RIGHT_CLICK_AIR -> {
                //
            }
            Action.LEFT_CLICK_AIR -> {
                //
            }
            else -> {
                //
            }
        }

    }

    @EventHandler
    fun onPlayerInteractEntity(event: PlayerInteractEntityEvent) {
        if (event.player != player) { return }
        if (!isBind) { return }
//        if (!isInChunk(event.rightClicked.location)) { event.isCancelled = true }
    }

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        if (event.player != player) { return }
        if (!isBind) { return }
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
    fun isInChunk(x: Int, z: Int, world: World): Boolean {
        // 플레이어가 위치하는 월드 기준으로 체크할 청크 가져오기
        val currentCheckChunk: Chunk;
        if (world.name == "world") { currentCheckChunk = overWorldChunk }
        else if (world.name == "world_nether") { currentCheckChunk = netherWorldChunk }
        else if (world.name == "world_the_end") { currentCheckChunk = theEndChunk }

        else { throw Error("\"${playerName}\" 플레이어가 위치한 월드 \"${world.name}\" 은/는 지원되지 않습니다") }

        // 청크 안에 있을경우
        if (currentCheckChunk.minX <= x && x < currentCheckChunk.maxX &&
            currentCheckChunk.minZ <= z && z < currentCheckChunk.maxZ) {
            return true
        }

        return false
    }

    fun toStringInfo(): String {
        return ""
    }

    val Chunk.minX: Int get() = this.x.toInt() * 16
    val Chunk.maxX: Int get() = this.x.toInt() * 16 + 16

    val Chunk.minZ: Int get() = this.z.toInt() * 16
    val Chunk.maxZ: Int get() = this.z.toInt() * 16 + 16
}
