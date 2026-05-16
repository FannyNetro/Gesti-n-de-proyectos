plugins {
    kotlin("jvm") version "1.9.22"
    id("io.ktor.plugin") version "2.3.8"
    kotlin("plugin.serialization") version "1.9.22"
}

group = "com.vgtech.backend"
version = "1.0.0"

application {
    mainClass.set("com.vgtech.backend.ApplicationKt")
}

repositories {
    mavenCentral()
}

val ktorVersion = "2.3.8"
val exposedVersion = "0.44.1"

dependencies {
    // ── Ktor Server ──────────────────────────────────────────────────
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-server-cors:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")

    // ── Base de datos ────────────────────────────────────────────────
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    implementation("org.postgresql:postgresql:42.7.1")
    implementation("com.zaxxer:HikariCP:5.0.1")

    // ── Logging ──────────────────────────────────────────────────────
    implementation("ch.qos.logback:logback-classic:1.4.14")
}
ktor {
    fatJar {
        archiveFileName.set("vgtech-backend.jar")
    }
}
