package com.github.ityeri.chunkHah

import com.github.ityeri.chunkHah.commands.AriaControlCommand
import com.github.ityeri.chunkHah.commands.BindingCommands
import com.github.ityeri.chunkHah.commands.ChunkManagerInfoCommands
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.io.FileNotFoundException
import com.github.ityeri.chunkHah.utils.HitboxUtils

// TODO 리소스 테스크

class ChunkHah : JavaPlugin() {
    private val hitboxUtils: HitboxUtils = HitboxUtils()
    val chunkHandler = ChunkHandler(this)


    override fun onEnable() {

        try {
            chunkHandler.loadAriaDatas()
        } catch (e: FileNotFoundException) {
            Bukkit.getLogger().warning("영역 데이터 파일을 찾을수 없습니다. 자동 할당을 사용합니다")
        } catch (e: ChunkHandler.WrongAriaDataException) {
            Bukkit.getLogger().warning("영역 데이터 파일의 구조가 잘못됬습니다. 자동 할당을 사용합니다")
        }

        // 명령어 등록
        BindingCommands(this, chunkHandler).onEnable()
        ChunkManagerInfoCommands(this, chunkHandler).onEnable()
        AriaControlCommand(this, chunkHandler).onEnable()

        // 엔더맨 거시시
        EndPortalFrameDropper(this).onEnable()

        chunkHandler.onEnable()

        Bukkit.getLogger().info("이것은 당신의 청크하가 매우 정상적으로 켜졌다는 의미일까요?")
    }

    override fun onDisable() {
        chunkHandler.saveAriaData()

    }
}
