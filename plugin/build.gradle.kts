import io.papermc.hangarpublishplugin.model.Platforms

plugins {
    id("java")
    id("io.papermc.hangar-publish-plugin") version "0.1.2"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("net.minecrell.plugin-yml.paper") version "0.6.0"
}

group = rootProject.group
version = "2.0.1"

repositories {
    mavenCentral()
    maven("https://repo.thenextlvl.net/releases")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.30")
    compileOnly("net.thenextlvl.core:annotations:2.0.1")
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")

    implementation(project(":api"))
    implementation(project(":v1_19_4", "reobf"))
    implementation(project(":v1_20_1", "reobf"))
    implementation(project(":v1_20_2", "reobf"))
    implementation(project(":v1_20_4", "reobf"))

    implementation("org.bstats:bstats-bukkit:3.0.2")

    annotationProcessor("org.projectlombok:lombok:1.18.30")
}

paper {
    name = "HologramAPI"
    main = "net.thenextlvl.hologram.HologramAPI"
    apiVersion = "1.19"
    website = "https://thenextlvl.net"
    authors = listOf("NonSwag")
}

tasks.shadowJar {
    minimize()
    archiveBaseName.set("holograms")
    relocate("org.bstats", "net.thenextlvl.hologram.bstats")
}

val versionString: String = project.version as String
val isRelease: Boolean = !versionString.contains("-pre")

hangarPublish { // docs - https://docs.papermc.io/misc/hangar-publishing
    publications.register("plugin") {
        id.set("HologramAPI")
        version.set(project.version as String)
        channel.set(if (isRelease) "Release" else "Snapshot")
        if (extra.has("HANGAR_API_TOKEN"))
            apiKey.set(extra["HANGAR_API_TOKEN"] as String)
        else apiKey.set(System.getenv("HANGAR_API_TOKEN"))
        platforms {
            register(Platforms.PAPER) {
                jar.set(tasks.shadowJar.flatMap { it.archiveFile })
                val versions: List<String> = (property("paperVersion") as String)
                    .split(",")
                    .map { it.trim() }
                platformVersions.set(versions)
            }
        }
    }
}
