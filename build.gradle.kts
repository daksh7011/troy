import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.20"
    kotlin("plugin.serialization") version "1.5.20"
    id("org.jmailen.kotlinter") version "3.4.5"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "in.technowolf"
version = "1.0.1"

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven {
        name = "Kotlin Discord"
        url = uri("https://maven.kotlindiscord.com/repository/maven-public/")
    }
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    implementation("dev.kord:kord-core:0.8.0-M7")
    implementation("com.kotlindiscord.kord.extensions:kord-extensions:1.5.1-RC1")
    implementation("com.kotlindiscord.kord.extensions:time4j:1.5.1-RC1")
    implementation("io.ktor:ktor-client-core:1.6.4")
    implementation("org.slf4j:slf4j-simple:1.7.32")
    implementation("org.jetbrains.exposed:exposed-core:0.36.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.36.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.36.1")
    implementation("org.xerial:sqlite-jdbc:3.36.0.2")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "MainKt"
    }
}

tasks.check {
    dependsOn("installKotlinterPrePushHook")
}

kotlinter {
    ignoreFailures = false
    indentSize = 4
    reporters = arrayOf("checkstyle", "plain")
    experimentalRules = false
    disabledRules = arrayOf("no-wildcard-imports", "import-ordering", "indent")
}
