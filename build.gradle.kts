import io.papermc.hangarpublishplugin.model.Platforms
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    id("java")
    id("com.gradleup.shadow") version "9.3.1"
    id("com.modrinth.minotaur") version "2.+"
    id("de.eldoria.plugin-yml.paper") version "0.8.0"
    id("io.papermc.hangar-publish-plugin") version "0.1.4"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.19"
}

group = "net.thenextlvl.holograms"

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

tasks.compileJava {
    options.release.set(21)
}

repositories {
    mavenCentral()
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.thenextlvl.net/releases")
    maven("https://repo.thenextlvl.net/snapshots")
}

dependencies {
    paperweight.foliaDevBundle("1.21.11-R0.1-SNAPSHOT")

    compileOnly("io.github.miniplaceholders:miniplaceholders-api:3.1.0")
    compileOnly("me.clip:placeholderapi:2.12.2")

    compileOnly("net.thenextlvl:vault-api:1.7.1")
    compileOnly("net.thenextlvl:service-io:2.5.1")

    implementation("net.thenextlvl.version-checker:modrinth-paper:1.0.1")
    implementation("net.thenextlvl:i18n:1.2.0")
    implementation("net.thenextlvl:nbt:4.3.4")
    implementation("net.thenextlvl:static-binder:0.1.3")

    implementation("dev.faststats.metrics:bukkit:0.14.0")
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
    foliaSupported = true
    permissions {
        register("holograms.command")
        register("holograms.command.create") { children = listOf("holograms.command") }
        register("holograms.command.delete") { children = listOf("holograms.command") }
        register("holograms.command.list") { children = listOf("holograms.command") }
    }
    serverDependencies {
        register("MiniPlaceholders") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = false
        }
        register("PlaceholderAPI") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = false
        }
        register("ServiceIO") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = false
        }
        register("Vault") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = false
        }
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
        changelog = System.getenv("CHANGELOG")
        channel.set(if (isRelease) "Release" else "Snapshot")
        apiKey.set(System.getenv("HANGAR_API_TOKEN"))
        platforms.register(Platforms.PAPER) {
            jar.set(tasks.shadowJar.flatMap { it.archiveFile })
            platformVersions.set(versions)
            dependencies {
                hangar("MiniPlaceholders") {
                    required.set(false)
                }
                hangar("PlaceholderAPI") {
                    required.set(false)
                }
                hangar("ServiceIO") {
                    required.set(false)
                }
                url("Vault", "https://www.spigotmc.org/resources/vault.34315/") {
                    required.set(false)
                }
            }
        }
    }
}

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN"))
    projectId.set("yWs5IQBz")
    changelog = System.getenv("CHANGELOG")
    versionType = if (isRelease) "release" else "beta"
    uploadFile.set(tasks.shadowJar)
    gameVersions.set(versions)
    syncBodyFrom.set(rootProject.file("README.md").readText())
    loaders.addAll((property("loaders") as String).split(",").map { it.trim() })
    dependencies {
        optional.project("miniplaceholders")
        optional.project("placeholderapi")
        optional.project("service-io")
    }
}