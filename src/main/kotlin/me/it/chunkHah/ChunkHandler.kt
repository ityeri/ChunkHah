
package me.it.chunkHah

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.world.WorldSaveEvent
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.FileWriter
import java.util.*
import kotlin.math.absoluteValue

/**
 * ChunkManager 객체를 할당하고, 관리하는 객체
 * /!\ ChunkManager 가 한청크에 하나씩 있다고 가정하고 동작함!
 */
class ChunkHandler(val plugin: JavaPlugin) : Listener {
    val chunkManagerList: MutableSet<ChunkManager> = mutableSetOf()

    class WrongAriaDataException(message: String) : RuntimeException(message)

    fun onEnable() {
        // 이벤트 해ㅑㄴ들러 등록
        Bukkit.getPluginManager().registerEvents(this, plugin)

        // 서버에 있는 인원 전부 메니저 만듦
        for (player in Bukkit.getOnlinePlayers()) {
            newChunkManager(player)?.onEnable()
        }
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        newChunkManager(event.player)?.onEnable()
    }

    @EventHandler
    fun onWorldSave(event: WorldSaveEvent) {
        saveAriaData()
    }


    fun loadAriaData() {
        val file = getAriaDataFile()

        if (!file.exists()) {
            throw FileNotFoundException("영역 데이터 파일 \"${file.path}\" 을/를 찾을수 없습니다")
        }

        val fileReader = FileReader(file)

        val inputData: JsonObject
        val gson = Gson()

        try {
            FileReader(file).use { reader ->
                inputData = Gson().fromJson(reader, JsonObject::class.java)!!
            }
        } catch (e: NullPointerException) {
            throw WrongAriaDataException("영역 데이터 파일: \"${file.path}\" 의 구조가 잘못되었습니다")
        }

        // 플레이어 UUID 하나하나 가져옴
        for (playerUUIDString in inputData.keySet()) {
            val chunkManagerData = inputData.get(playerUUIDString) as JsonObject
            /*
            chunkManagerData 구조:
            {
                "world": {"x": 0, "z": 1},
                "world_nether": {"x": 2, "z": 3},
                "world_the_end": {"x": 4, "z": 5}
            }
             */

            val overWorldChunk: Chunk
            val netherWorldChunk: Chunk
            val theEndChunk: Chunk

            try {
                // 오버월드
                val overWorldData = gson.fromJson(chunkManagerData.get("world"), Map::class.java)
                        as Map<String, Int>
                overWorldChunk = Bukkit.getWorld("world")!!
                    .getChunkAt(overWorldData.get("x")!!, overWorldData.get("z")!!)

                // 네더월드
                val netherWorldData = gson.fromJson(chunkManagerData.get("world_nether"), Map::class.java)
                        as Map<String, Int>
                netherWorldChunk = Bukkit.getWorld("world_nether")!!
                    .getChunkAt(netherWorldData.get("x")!!, netherWorldData.get("z")!!)

                // 날파리월드
                val theEndData = gson.fromJson(chunkManagerData.get("world_the_end"), Map::class.java)
                        as Map<String, Int>
                theEndChunk = Bukkit.getWorld("world_the_end")!!
                    .getChunkAt(netherWorldData.get("x")!!, netherWorldData.get("z")!!)

            } catch (e: java.lang.NullPointerException) {
                Bukkit.getLogger().warning(
                    "사용자 UUID: \"$playerUUIDString\" 의 청크 데이터가 잘못되었습니다"
                )
                continue
            }


            // 이미 매니저가 있을경우 기존 매니저의 청크를 변경하고,
            // 매지너가 없으면 새로 만듦
            val playerUUID = UUID.fromString(playerUUIDString)

            if (isChunkManagerExist(playerUUID)) {
                getChunkManager(playerUUID)!!.overWorldChunk = overWorldChunk
                getChunkManager(playerUUID)!!.netherWorldChunk = netherWorldChunk
                getChunkManager(playerUUID)!!.theEndChunk = theEndChunk
            } else {
                val chunkManager = ChunkManager(
                    plugin, UUID.fromString(playerUUIDString),
                    overWorldChunk, netherWorldChunk, theEndChunk
                )
                addChunkManager(
                    chunkManager
                )
                chunkManager.onEnable()
            }
        }

    }

    fun saveAriaData() {
        val file = getAriaDataFile()

        if (!file.exists()) {
            file.parentFile.mkdirs()
            file.createNewFile()
        }

        val fileWriter = FileWriter(file)

        val outputData = JsonObject()
        /*
        outputData 구조:
        {
            "플레이어 UUID": {
                "world": {"x": 0, "z": 1},
                "world_nether": {"x": 2, "z": 3},
                "world_the_end": {"x": 4, "z": 5}
            },
            "다른 플레이어 UUID": ...
        }
        */


        for (chunkManager in chunkManagerList) {
            val chunkManagerData = JsonObject()
            /*
            chunkManagerData 구조:
            {
                "world": {"x": 0, "z": 1},
                "world_nether": {"x": 2, "z": 3},
                "world_the_end": {"x": 4, "z": 5}
            }
             */

            val overWorldChunkData = JsonObject()
            overWorldChunkData.addProperty("x", chunkManager.overWorldChunk.x)
            overWorldChunkData.addProperty("z", chunkManager.overWorldChunk.z)

            val netherWorldChunkData = JsonObject()
            netherWorldChunkData.addProperty("x", chunkManager.netherWorldChunk.x)
            netherWorldChunkData.addProperty("z", chunkManager.netherWorldChunk.z)

            val theEndChunkData = JsonObject()
            theEndChunkData.addProperty("x", chunkManager.theEndChunk.x)
            theEndChunkData.addProperty("z", chunkManager.theEndChunk.z)

            chunkManagerData.add("world", overWorldChunkData)
            chunkManagerData.add("world_nether", netherWorldChunkData)
            chunkManagerData.add("world_the_end", theEndChunkData)

            outputData.add(chunkManager.playerUUID.toString(), chunkManagerData)
        }

        // 파일의 가독성을 설정하여 저장
        GsonBuilder().setPrettyPrinting().create()
            .toJson(outputData, fileWriter)

        fileWriter.close()
    }


    fun getAriaDataFile(): File {
        // config.json 에 저장된 영역 데이터 불러오기
        return File(plugin.dataFolder, "aria.json")
    }


    fun newChunkManager(player: Player): ChunkManager? {
        // 이미 해당 플레이어에 대한 매니저가 있을경우 작업 안해도 됨
        if (isChunkManagerExist(player)) { return null }



        // 중앙으로부터 나선을 그려가며
        // 점점 바깥쪽으로 나가면서 빈 청크를 찾음
        // 내가 옜날에 해둔 나선 알고리즘 구조도.txt 참고한거라
        // 자세한건 지금의 나도 모르는;;
        val overWorldChunk: Chunk;
        val netherWorldChunk: Chunk;
        val theEndChunk: Chunk;

        var x: Int
        var y: Int

        // 오버월드 할당
        x = 0
        y = 0

        while (true) {
            if (x.absoluteValue <= y) x -= 1
            else if (y.absoluteValue <= -x-1) y += 1
            else if ((x.absoluteValue <= -y && x < 0) || (x.absoluteValue <= -y-1 && 0 <= x)) x += 1
            else y -= 1

            val currentCheckingChunk = Bukkit.getWorld("world")!!.getChunkAt(x, y)

            if (!isChunkManagerExist(currentCheckingChunk)) {
                overWorldChunk = currentCheckingChunk
                break
            }
        }

        // 네더월드 할당
        x = 0
        y = 0

        while (true) {
            if (x.absoluteValue <= y) x -= 1
            else if (y.absoluteValue <= -x-1) y += 1
            else if ((x.absoluteValue <= -y && x < 0) || (x.absoluteValue <= -y-1 && 0 <= x)) x += 1
            else y -= 1

            val currentCheckingChunk = Bukkit.getWorld("world_nether")!!.getChunkAt(x, y)

            if (!isChunkManagerExist(currentCheckingChunk)) {
                netherWorldChunk = currentCheckingChunk
                break
            }
        }

        // 오버월드 할당
        x = 0
        y = 0

        while (true) {
            if (x.absoluteValue <= y) x -= 1
            else if (y.absoluteValue <= -x-1) y += 1
            else if ((x.absoluteValue <= -y && x < 0) || (x.absoluteValue <= -y-1 && 0 <= x)) x += 1
            else y -= 1

            val currentCheckingChunk = Bukkit.getWorld("world_the_end")!!.getChunkAt(x, y)

            if (!isChunkManagerExist(currentCheckingChunk)) {
                theEndChunk = currentCheckingChunk
                break
            }
        }

        val chunkManager = ChunkManager(plugin,
            player = player,
            overWorldChunk = overWorldChunk,
            netherWorldChunk = netherWorldChunk,
            theEndChunk = theEndChunk
        )

        addChunkManager(chunkManager)

        return chunkManager
    }

    fun addChunkManager(chunkManager: ChunkManager) {
        chunkManagerList.add(chunkManager)
    }

    fun isChunkManagerExist(player: Player): Boolean {
        var isExist = false
        for (chunkManager in chunkManagerList) {
            if (chunkManager.playerUUID == player.uniqueId) {
                isExist = true
                break
            }
        }
        return isExist
    }
    fun isChunkManagerExist(playerUUID: UUID): Boolean {
        var isExist = false
        for (chunkManager in chunkManagerList) {
            if (chunkManager.playerUUID == playerUUID) {
                isExist = true
                break
            }
        }
        return isExist
    }
    fun isChunkManagerExist(chunk: Chunk): Boolean {
        var isExist = false
        for (chunkManager in chunkManagerList) {
            if (chunkManager.overWorldChunk == chunk ||
                chunkManager.netherWorldChunk == chunk ||
                chunkManager.theEndChunk == chunk) {
                isExist = true
                break
            }
        }
        return isExist
    }

    fun getChunkManager(player: Player): ChunkManager? {
        for (chunkManager in chunkManagerList) {
            if (chunkManager.playerUUID == player.uniqueId) {
                return chunkManager
            }
        }
        return null
    }
    fun getChunkManager(playerUUID: UUID): ChunkManager? {
        for (chunkManager in chunkManagerList) {
            if (chunkManager.playerUUID == playerUUID) {
                return chunkManager
            }
        }
        return null
    }
    fun getChunkManager(chunk: Chunk): ChunkManager? {
        for (chunkManager in chunkManagerList) {
            if (chunkManager.overWorldChunk == chunk ||
                chunkManager.netherWorldChunk == chunk ||
                chunkManager.theEndChunk == chunk) {
                return chunkManager
            }
        }
        return null
    }
}