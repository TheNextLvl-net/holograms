plugins {
    id("java")
    id("maven-publish")
}

java {
    withSourcesJar()
    withJavadocJar()
}

group = rootProject.group
version = "1.0.2"

repositories {
    mavenCentral()
    maven("https://repo.thenextlvl.net/releases")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.28")
    compileOnly("io.papermc.paper:paper-api:1.19.4-R0.1-SNAPSHOT")

    compileOnly("net.thenextlvl.core:annotations:2.0.0")

    annotationProcessor("org.projectlombok:lombok:1.18.28")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
        repositories {
            maven {
                val channel = if ((version as String).contains("-pre")) "snapshots" else "releases"
                url = uri("https://repo.thenextlvl.net/$channel")
                credentials {
                    if (extra.has("RELEASES_USER"))
                        username = extra["RELEASES_USER"].toString()
                    if (extra.has("RELEASES_PASSWORD"))
                        password = extra["RELEASES_PASSWORD"].toString()
                }
            }
        }
    }
}