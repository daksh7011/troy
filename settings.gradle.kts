pluginManagement {
    plugins {
        // Update this in libs.version.toml when you change it here.
        kotlin("jvm") version "2.1.0"
        kotlin("plugin.serialization") version "2.1.0"

        // Update this in libs.version.toml when you change it here.
        id("io.gitlab.arturbosch.detekt") version "1.23.8"

        id("com.github.jakemarsden.git-hooks") version "0.0.2"
        id("com.github.johnrengelman.shadow") version "8.1.1"

        id("dev.kordex.gradle.docker") version "1.6.1"
        id("dev.kordex.gradle.kordex") version "1.6.2"
    }

    repositories {
        gradlePluginPortal()
        mavenCentral()

        maven("https://snapshots-repo.kordex.dev")
        maven("https://releases-repo.kordex.dev")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
    }
}

rootProject.name = "troy"
