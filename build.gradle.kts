buildscript {
    dependencyLocking {
        lockAllConfigurations()
    }

    dependencies.components.all {
        if (Regex("(?i).+([-.])(CANDIDATE|RC|BETA|ALPHA|M\\d+).*").matches(id.version)) {
            status = "milestone"
        }
    }
}

plugins {
    java
    id("org.springframework.boot") version "latest.release"
    id("io.spring.dependency-management") version "latest.release"
}

dependencyLocking {
    lockAllConfigurations()
    dependencies.components.all {
        if (Regex("(?i).+([-.])(CANDIDATE|RC|BETA|ALPHA|M\\d+).*").matches(id.version)) {
            status = "milestone"
        }
    }
}

group = "de.cronn"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    runtimeOnly("org.postgresql:postgresql")

    testImplementation("de.cronn:postgres-snapshot-util:latest.release")
    testImplementation("de.cronn:validation-file-assertions:latest.release")
    testImplementation("de.cronn:test-utils:latest.release")
    testImplementation("de.cronn:commons-lang:latest.release")
    testImplementation("com.github.gavlyukovskiy:datasource-proxy-spring-boot-starter:latest.release")

    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter")
    testImplementation("org.testcontainers:testcontainers-postgresql")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
