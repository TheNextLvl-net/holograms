plugins {
    id("java")
    id("io.papermc.paperweight.userdev") version "1.5.11"
}

group = rootProject.group
version = project(":api").version

repositories {
    mavenCentral()
    maven("https://repo.thenextlvl.net/releases")
}

dependencies {
    paperweight.paperDevBundle("1.20.2-R0.1-SNAPSHOT")

    compileOnly("org.projectlombok:lombok:1.18.28")
    compileOnly("net.thenextlvl.core:annotations:2.0.1")

    implementation(project(":api"))

    annotationProcessor("org.projectlombok:lombok:1.18.28")
}

tasks {
    task("reobf") {
        dependsOn(reobfJar)
    }
}
