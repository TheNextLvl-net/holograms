import io.papermc.hangarpublishplugin.model.Platforms
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    id("java")
    id("com.gradleup.shadow") version "9.0.0-beta17"
    id("de.eldoria.plugin-yml.paper") version "0.7.1"
    id("io.papermc.hangar-publish-plugin") version "0.1.3"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.17"
}

group = "net.thenextlvl.holograms"
version = "3.0.0"

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

tasks.compileJava {
    options.release.set(21)
}

repositories {
    mavenCentral()
    maven("https://repo.thenextlvl.net/releases")
}

dependencies {
    paperweight.paperDevBundle("1.21.5-R0.1-SNAPSHOT")
    implementation("net.thenextlvl.core:nbt:2.3.2")
    implementation("org.bstats:bstats-bukkit:3.1.0")
    implementation(project(":api"))
}

paper {
    name = "Holograms"
    main = "net.thenextlvl.hologram.HologramPlugin"
    apiVersion = "1.21"
    load = BukkitPluginDescription.PluginLoadOrder.STARTUP
    website = "https://thenextlvl.net"
    authors = listOf("NonSwag")
    permissions {
        register("holograms.command")
        register("holograms.command.create") { children = listOf("holograms.command") }
    }
}

tasks.shadowJar {
    minimize()
    archiveBaseName.set("holograms")
    relocate("org.bstats", "net.thenextlvl.hologram.bstats")
}

val versionString: String = project.version as String
val isRelease: Boolean = !versionString.contains("-pre")

val versions: List<String> = (property("gameVersions") as String)
    .split(",")
    .map { it.trim() }

hangarPublish { // docs - https://docs.papermc.io/misc/hangar-publishing
    publications.register("plugin") {
        id.set("Holograms")
        version.set(versionString)
        channel.set(if (isRelease) "Release" else "Snapshot")
        apiKey.set(System.getenv("HANGAR_API_TOKEN"))
        platforms.register(Platforms.PAPER) {
            jar.set(tasks.shadowJar.flatMap { it.archiveFile })
            platformVersions.set(versions)
        }
    }
}
