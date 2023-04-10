plugins {
    id("java")
    id("io.papermc.paperweight.userdev") version "1.5.3"
}

group = "net.thenextlvl.holograms"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.thenextlvl.net/releases")
}

dependencies {
    paperweight.paperDevBundle("1.19.4-R0.1-SNAPSHOT")

    compileOnly("org.projectlombok:lombok:1.18.26")

    implementation("net.thenextlvl.core:core-api:3.1.10")
    implementation(project(":api"))

    annotationProcessor("org.projectlombok:lombok:1.18.26")
}
