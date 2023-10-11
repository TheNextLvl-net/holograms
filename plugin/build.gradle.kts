plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("net.minecrell.plugin-yml.paper") version "0.6.0"
}

group = rootProject.group
version = "1.0.5"

repositories {
    mavenCentral()
    maven("https://repo.thenextlvl.net/releases")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.28")
    compileOnly("net.thenextlvl.core:annotations:2.0.0")
    compileOnly("io.papermc.paper:paper-api:1.19.4-R0.1-SNAPSHOT")

    implementation(project(":api"))
    implementation(project(":v1_19_4", "reobf"))
    implementation(project(":v1_20_1", "reobf"))
    implementation(project(":v1_20_2", "reobf"))

    annotationProcessor("org.projectlombok:lombok:1.18.28")
}

paper {
    name = "HologramAPI"
    main = "net.thenextlvl.hologram.HologramAPI"
    apiVersion = "1.19"
    website = "https://thenextlvl.net"
    authors = listOf("NonSwag")
}

tasks {
    assemble {
        dependsOn(shadowJar)
    }
}
