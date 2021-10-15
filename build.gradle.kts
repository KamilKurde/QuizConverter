import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.31"
    kotlin("plugin.serialization") version "1.5.31"
    application
}

group = "com.github.KamilKurde"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies{
    implementation("org.apache.poi:poi:5.0.0")
    implementation("org.apache.poi:poi-ooxml:5.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "10"
}

application {
    mainClass.set("MainKt")
}