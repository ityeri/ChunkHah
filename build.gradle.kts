plugins {
    kotlin("jvm") version "2.1.20-Beta1"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

tasks {
    jar {
        destinationDirectory.set(file("D:\\mcServer\\1.21_1.21.1\\청크하 테스트\\plugins"))
    }
}

group = "com.github.ityeri"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
    maven ("https://repo.aikar.co/content/groups/aikar/")
    maven ("https://hub.spigotmc.org/nexus/content/groups/public/")
}


dependencies {
    // 패이퍼 / 스피갓 / 코틀린
    compileOnly("io.papermc.paper:paper-api:1.21.5-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    compileOnly("org.spigotmc:spigot-api:1.15.2-R0.1-SNAPSHOT")

    // CommandFramework
    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")
}


val targetJavaVersion = 21
kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks.build {
    dependsOn("shadowJar")
}

tasks.shadowJar {
    relocate("co.aikar.commands", "me.it.chunkHah.acf")
    relocate("co.aikar.locales", "me.it.chunkHah.locales")
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

tasks.jar {
    manifest {
        attributes["Implementation-Version"] = version
    }
    from(configurations.compileClasspath.get().filter { it.name.endsWith("kotlin-stdlib.jar") }.map { if (it.isDirectory) it else zipTree(it) })

    // To avoid the duplicate handling strategy error
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    // To add all of the dependencies
    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })

}
