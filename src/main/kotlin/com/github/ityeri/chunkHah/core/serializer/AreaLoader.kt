package com.github.ityeri.chunkHah.core.serializer

import com.github.ityeri.chunkHah.core.AreaManager
import kotlinx.serialization.json.*
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.WorldSaveEvent
import java.io.File
import java.io.FileNotFoundException

class AreaLoader(val areaManager: AreaManager) : Listener {

    fun enable() {
        Bukkit.getPluginManager().registerEvents(this, areaManager.plugin)
    }

    fun save() {

        val serializedAreas = buildJsonArray {
            for (area in areaManager.getAllArea()) {
                add(Json.encodeToJsonElement(AreaSerializer, area))
            }
        }

        val data = buildJsonObject {
            put("areaWidth", areaManager.areaWidth)
            put("areaDepth", areaManager.areaDepth)
            put("areas", serializedAreas)
        }

        val file = File(areaManager.plugin.dataFolder, "area_data.json")

        file.parentFile?.mkdirs()

        if (!file.exists()) {
            file.createNewFile()
        }

        val json = Json {
            prettyPrint = true
            prettyPrintIndent = "    "
        }

        file.writeText(json.encodeToString(JsonObject.serializer(), data))
    }

    fun load() {
        val file = File(areaManager.plugin.dataFolder, "area_data.json")
        if (!file.exists()) {
            throw FileNotFoundException("영역파일 area_data.json 이 존재하지 않습니다")
        }

        val data = Json.parseToJsonElement(file.readText()) as JsonObject

        areaManager.areaWidth = data["areaWidth"]!!.jsonPrimitive.int
        areaManager.areaDepth = data["areaDepth"]!!.jsonPrimitive.int

        areaManager.removeAllArea()

        data["areas"]!!.jsonArray.forEach {
            areaManager.addArea(Json.decodeFromJsonElement(AreaSerializer, it))
        }
    }

    @EventHandler
    fun onWorldSave(event: WorldSaveEvent) {
        save()
    }
}