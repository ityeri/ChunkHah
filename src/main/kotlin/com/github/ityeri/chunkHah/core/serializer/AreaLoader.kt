package com.github.ityeri.chunkHah.core.serializer

import com.github.ityeri.chunkHah.core.AreaManager
import kotlinx.serialization.json.*
import java.io.File

class AreaLoader(val areaManager: AreaManager) {
    fun save() {

        val json = Json {
            prettyPrint = true
            prettyPrintIndent = "    "
        }

        val serializedAreas = buildJsonArray {
            for (area in areaManager.getAllArea()) {
                add(json.encodeToString(AreaSerializer, area))
            }
        }

        val data = buildJsonObject {
            put("areaWidth", areaManager.areaWidth)
            put("areaDepth", areaManager.areaDepth)
            put("areas", serializedAreas)
        }

        val file = File(areaManager.plugin.dataFolder, "area_data.json")
        file.writeText(json.encodeToString(JsonObject.serializer(), data))
    }
}