
package com.github.ityeri.chunkHah

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
 * /!\
 * 한 플레이어에 대해선 청크 매니저는 하나씩만 있음!
 * 다만 하나에 청크에 대해선 서로 다른 여러명의 플레이어의 청크 매니저가 있을수 있음
 * /!\
 */
class ChunkHandler(val plugin: JavaPlugin) : Listener {
    val chunkManagerSet: MutableSet<ChunkManager> = mutableSetOf()

    class DuplicatePlayerException(message: String) : Exception(message)

    class WrongAriaDataException(message: String) : RuntimeException(message)

    fun onEnable() {
        // 이벤트 해ㅑㄴ들러 등록
        Bukkit.getPluginManager().registerEvents(this, plugin)

        // 서버에 있는 인원 전부 메니저 만듦
        for (player in Bukkit.getOnlinePlayers()) {
            newChunkManager(player)
        }

        Bukkit.getScheduler().runTaskTimer(plugin, Runnable {
            for (chunkManager in chunkManagerSet) {
                chunkManager.update()
            }
        }, 0, 1)
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        newChunkManager(event.player)
    }

    @EventHandler
    fun onWorldSave(event: WorldSaveEvent) {
        saveAriaData()
    }



    fun loadAriaDatas() {
        val file = getAriaDataFile()
        chunkManagerSet.clear()

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

            // 이미 매니저가 있을경우 기존 매니저의 청크를 변경하고,
            // 매지너가 없으면 새로 만듦
            val playerUUID = UUID.fromString(playerUUIDString)

            // 청크 매니저가 있다면 기존걸 지우고 새로 만듦
            if (isChunkManagerExist(playerUUID)) {
                println("로드 메서드에서 삭에 호출")
                println(Bukkit.getPlayer(playerUUID)!!.name)
                removeManager(playerUUID)

                val chunkManager = ChunkManager.fromJsonObject(chunkManagerData, plugin, playerUUID)
                addChunkManager(chunkManager)

                // 매니저가 없다면 걍 새로 만듦
            } else {
                val chunkManager = ChunkManager.fromJsonObject(chunkManagerData, plugin, playerUUID)
                addChunkManager(chunkManager)
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


        for (chunkManager in chunkManagerSet) {
            outputData.add(chunkManager.playerUUID.toString(), chunkManager.toJsonObject())
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
        for (originChunkManager in chunkManagerSet) {
            if (originChunkManager.playerUUID == chunkManager.playerUUID) {
                throw DuplicatePlayerException("이미 동일한 플레이어의 청크 매니저가 존재 합니다")
            }
        }
        chunkManagerSet.add(chunkManager)
    }

    fun isChunkManagerExist(player: Player): Boolean {
        for (chunkManager in chunkManagerSet) {
            if (chunkManager.playerUUID == player.uniqueId) {
                return true
            }
        }
        return false
    }
    fun isChunkManagerExist(playerUUID: UUID): Boolean {
        for (chunkManager in chunkManagerSet) {
            if (chunkManager.playerUUID == playerUUID) {
                return true
            }
        }
        return false
    }
    fun isChunkManagerExist(chunk: Chunk): Boolean {
        for (chunkManager in chunkManagerSet) {
            if (chunkManager.overWorldChunk == chunk ||
                chunkManager.netherWorldChunk == chunk ||
                chunkManager.theEndChunk == chunk) {
                return true
            }
        }
        return false
    }

    fun getChunkManager(player: Player): ChunkManager? {
        for (chunkManager in this.chunkManagerSet) {
            if (chunkManager.playerUUID == player.uniqueId) {
                return chunkManager
            }
        }
        return null
    }
    fun getChunkManager(playerUUID: UUID): ChunkManager? {
        for (chunkManager in chunkManagerSet) {
            if (chunkManager.playerUUID == playerUUID) {
                return chunkManager
            }
        }
        return null
    }
    fun getChunkManager(chunk: Chunk): MutableSet<ChunkManager> {
        val chunkManagerSet: MutableSet<ChunkManager> = mutableSetOf()
        for (chunkManager in this.chunkManagerSet) {
            if (chunkManager.overWorldChunk == chunk ||
                chunkManager.netherWorldChunk == chunk ||
                chunkManager.theEndChunk == chunk) {
                chunkManagerSet.add(chunkManager)
            }
        }
        return chunkManagerSet
    }

    fun removeManager(playerUUID: UUID) {
        var isFound = false
        for (chunkManager in chunkManagerSet) {
            if (chunkManager.playerUUID == playerUUID) {
                chunkManagerSet.remove(chunkManager)
                isFound = true
            }
        }

        if (!isFound) { throw NoSuchElementException("해당 UUID 를 가진 플레이어를 찾을수 없습니다") }
    }
}