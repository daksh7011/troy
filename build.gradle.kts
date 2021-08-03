import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.10"
}

group = "in.technowolf"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven {
        name = "Kotlin Discord"
        url = uri("https://maven.kotlindiscord.com/repository/maven-public/")
    }
}

dependencies {
    implementation("dev.kord:kord-core:0.7.4")
    implementation("com.kotlindiscord.kord.extensions:kord-extensions:1.4.1")
    implementation("org.slf4j:slf4j-simple:1.7.30")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}