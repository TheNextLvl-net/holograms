plugins {
    id("java")
}

group = "net.thenextlvl.holograms"

allprojects {
    group = rootProject.group
    version = rootProject.version

    apply {
        plugin("java")
    }

    java {
        toolchain.languageVersion = JavaLanguageVersion.of(25)
    }

    tasks.compileJava {
        options.release.set(21)
    }

    configurations.compileClasspath {
        attributes.attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 25)
    }

    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.thenextlvl.net/releases")
    }
}
