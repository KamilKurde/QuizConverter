import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.31"
    kotlin("plugin.serialization") version "1.5.31"
    application
}

group = "com.github.KamilKurde"
version = "2.0"

repositories {
    mavenCentral()
}

dependencies{
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")
}

tasks.withType<Jar> {
    manifest { attributes["Main-Class"] = application.mainClass }

    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    configurations["compileClasspath"].forEach { file: File -> from(zipTree(file.absoluteFile)) }
}

tasks.withType<KotlinCompile> { kotlinOptions.jvmTarget = "1.8" }

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

application {
    mainClass.set("MainKt")
}