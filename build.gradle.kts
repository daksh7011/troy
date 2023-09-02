import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.22"
    kotlin("plugin.serialization") version "1.8.21"
    id("org.jmailen.kotlinter") version "3.15.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "in.technowolf"
version = "1.2.1"

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven {
        name = "Kotlin Discord"
        url = uri("https://maven.kotlindiscord.com/repository/maven-public/")
    }
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.kotlindiscord.kord.extensions:kord-extensions:1.5.8-SNAPSHOT")
    implementation("com.kotlindiscord.kord.extensions:time4j:1.5.8-SNAPSHOT")
    implementation("org.slf4j:slf4j-simple:2.0.7")
//    implementation("net.dean.jraw:JRAW:1.1.0")
    implementation(files("libs/JRAW-1.1.0.jar"))
    implementation("io.getunleash:unleash-client-java:8.1.0")
    implementation("dev.kord.x:emoji:0.5.0")
    implementation("org.litote.kmongo:kmongo-coroutine:4.10.0")
    implementation("com.gitlab.technowolf:links-detektor:1.0.1")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.2")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

tasks.withType(KotlinCompile::class).all {
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
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
    reporters = arrayOf("checkstyle", "plain")
}
