import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "4.0.0"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.jlleitschuh.gradle.ktlint") version "14.0.1"
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.spring") version "2.2.21"
}

group = "no.nav.syfo"
version = "1.0.0"
description = "aktivitetskrav-backend"
java.sourceCompatibility = JavaVersion.VERSION_21

ext["okhttp3.version"] = "4.11.0"

repositories {
    mavenCentral()
    maven {
        url = uri("https://github-package-registry-mirror.gc.nav.no/cached/maven-release")
    }
}

val tokenSupportVersion = "6.0.0"
val logstashLogbackEncoderVersion = "9.0"
val inntektsmeldingKontraktVersion = "2023.09.21-02-30-3f310"
val sykepengesoknadKafkaVersion = "2023.09.27-13.04-8327d8dd"
val mockkVersion = "1.14.7"
val kotestVersion = "6.0.7"
val kotestExtensionsVersion = "2.0.0"
val hikariVersion = "7.0.2"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.springframework.kafka:spring-kafka-test")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-flyway")
    implementation("no.nav.security:token-client-spring:$tokenSupportVersion")
    implementation("no.nav.security:token-validation-spring:$tokenSupportVersion")
    implementation("org.slf4j:slf4j-api")
    implementation("org.springframework.boot:spring-boot-starter-logging")
    implementation("net.logstash.logback:logstash-logback-encoder:$logstashLogbackEncoderVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("org.postgresql:postgresql")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")
    implementation("org.hibernate.validator:hibernate-validator")
    implementation("com.zaxxer:HikariCP:$hikariVersion")

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-registry-prometheus")

    testImplementation("no.nav.security:token-validation-spring-test:$tokenSupportVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest:kotest-property:$kotestVersion")
    testImplementation("io.kotest.extensions:kotest-assertions-ktor:$kotestExtensionsVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "junit")
    }
    testImplementation("com.h2database:h2:2.4.240")
}

tasks {

    extra["snakeyaml.version"] = "2.2"

    named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
        this.archiveFileName.set("app.jar")
    }

    withType<KotlinCompile> {
        compilerOptions {
            freeCompilerArgs.set(listOf("-Xjsr305=strict"))
            jvmTarget.set(JvmTarget.JVM_21)
            if (System.getenv("CI") == "true") {
                compilerOptions.allWarningsAsErrors.set(true)
            }
        }
    }

    named<Jar>("jar") {
        enabled = false
    }

    named("check") {
        dependsOn("ktlintCheck")
    }

    withType<Test> {
        useJUnitPlatform()
        testLogging {
            events("STANDARD_OUT", "STARTED", "PASSED", "FAILED", "SKIPPED")
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        }
    }
}
