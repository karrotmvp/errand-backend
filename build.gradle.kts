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
    runtimeOnly("com.fasterxml.jackson.module:jackson-modules-java8:2.13.0")

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

    implementation("org.springframework.cloud:spring-cloud-starter-aws:2.2.6.RELEASE")

    // async s3 client
    implementation("software.amazon.awssdk:regions:2.17.81")
    implementation("software.amazon.awssdk:core:2.17.81")
    implementation("software.amazon.awssdk:s3:2.17.81")

    implementation("com.squareup.okhttp3:okhttp:4.9.1")
    implementation("com.squareup.okhttp3:logging-interceptor:4.5.0")

    implementation("io.jsonwebtoken:jjwt:0.9.1")

    val querydslVersion = "4.4.0"
    implementation("com.querydsl:querydsl-jpa:$querydslVersion")
    kapt("com.querydsl:querydsl-apt:$querydslVersion:jpa")

    implementation("org.springframework.boot:spring-boot-starter-data-redis:2.5.5")
    implementation("io.sentry:sentry-spring-boot-starter:5.2.4")
    implementation("com.mixpanel:mixpanel-java:1.5.0")

    kapt("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor ("org.springframework.boot:spring-boot-configuration-processor")

    // admin static pages
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-devtools")

    implementation("com.datadoghq:dd-trace-api:0.90.0")
    implementation("com.datadoghq:dd-trace-ot:0.90.0")

    testImplementation("io.mockk:mockk:1.12.1")
    testImplementation("com.github.javafaker:javafaker:1.0.2")
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
