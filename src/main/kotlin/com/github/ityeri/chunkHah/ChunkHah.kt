package com.github.ityeri.chunkHah

import com.github.ityeri.chunkHah.commands.BindCommand
import com.github.ityeri.chunkHah.commands.PlayerChunkInfo
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.io.FileNotFoundException
import com.github.ityeri.chunkHah.utils.HitboxUtils
import net.kyori.adventure.text.Component

// TODO 명려어 만들기 이것저것
// 영역 재할당, aria 로드, 세이브
// 기타 편의성 거시기
// 권한 설정? 기타 등등
// 리소스 테스크, 엔딩 볼수 있게 만ㄷ르기

class ChunkHah : JavaPlugin() {
    private val hitboxUtils: HitboxUtils = HitboxUtils()
    val chunkHandler = ChunkHandler(this)

    override fun onEnable() {

        Bukkit.getServer().sendMessage(Component.text("asdf"))

        chunkHandler.onEnable()

        try {
            chunkHandler.loadAriaData()
        } catch (e: FileNotFoundException) {
            Bukkit.getLogger().warning("영역 데이터 파일을 찾을수 없습니다. 자동 할당을 사용합니다")
        } catch (e: ChunkHandler.WrongAriaDataException) {
            Bukkit.getLogger().warning("영역 데이터 파일의 구조가 잘못됬습니다. 자동 할당을 사용합니다")
        }

        // 명령어 등록
        BindCommand(this, chunkHandler).onEnable()
        PlayerChunkInfo(this, chunkHandler).onEnable()


        Bukkit.getLogger().info("이것은 당신의 청크하가 매우 정상적으로 켜졌다는 의미일까요?")
    }

    override fun onDisable() {
        chunkHandler.saveAriaData()

    }
}
