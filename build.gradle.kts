import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
tasks {
    bootJar {
        archiveFileName.set("application.jar")
    }
}

plugins {

    val kotlinVersion = "1.5.31"
    id("org.springframework.boot") version "2.5.5"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"

    id ("org.jetbrains.kotlin.plugin.allopen") version kotlinVersion
    id ("org.jetbrains.kotlin.plugin.noarg") version kotlinVersion

    id ("org.jetbrains.kotlin.plugin.jpa") version kotlinVersion

    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("kapt") version kotlinVersion
}

allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.MappedSuperclass")
    annotation("javax.persistence.Embeddable")
}

group = "com.daangn"
version = "0.0.2-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.github.consoleau:kassava:2.1.0")

    implementation("io.github.microutils:kotlin-logging-jvm:2.0.10")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    testImplementation("com.h2database:h2")

    runtimeOnly("mysql:mysql-connector-java")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.h2database:h2")

    implementation("io.springfox:springfox-boot-starter:3.0.0")

    val mapstructVersion = "1.4.2.Final"
    implementation("org.mapstruct:mapstruct:$mapstructVersion")
    annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")
    kapt("org.mapstruct:mapstruct-processor:$mapstructVersion")
    testImplementation("org.mapstruct:mapstruct-processor:$mapstructVersion")

    // https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-aws
    implementation("org.springframework.cloud:spring-cloud-starter-aws:2.2.6.RELEASE")

//    implementation("org.springframework.boot:spring-boot-starter-security:2.5.5")
//    implementation("org.springframework.security:spring-security-oauth2-client:5.5.2")

    implementation("com.squareup.okhttp3:okhttp:4.9.1")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
