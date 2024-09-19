import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.3.4"
    id("io.spring.dependency-management") version "1.1.6"
    id("io.gitlab.arturbosch.detekt") version "1.23.6"
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.spring") version "1.9.23"
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

val testContainersVersion = "1.19.0"
val tokenSupportVersion = "5.0.5"
val logstashLogbackEncoderVersion = "7.4"
val kluentVersion = "1.73"
val inntektsmeldingKontraktVersion = "2023.09.21-02-30-3f310"
val sykepengesoknadKafkaVersion = "2023.09.27-13.04-8327d8dd"
val mockkVersion = "1.13.12"
val kotestVersion = "5.9.1"
val kotestExtensionsVersion = "2.0.0"
val hikariVersion = "5.1.0"
val detektVersion = "1.23.7"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.springframework.kafka:spring-kafka-test")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("no.nav.security:token-client-spring:$tokenSupportVersion")
    implementation("no.nav.security:token-validation-spring:$tokenSupportVersion")
    implementation("org.slf4j:slf4j-api")
    implementation("org.springframework.boot:spring-boot-starter-logging")
    implementation("net.logstash.logback:logstash-logback-encoder:$logstashLogbackEncoderVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
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
    testImplementation("com.h2database:h2:2.3.232")

    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:$detektVersion")

    constraints {
        implementation("org.apache.zookeeper:zookeeper") {
            because("CVE-2023-44981")
            version {
                require("3.8.3")
            }
        }
        implementation("org.xerial.snappy:snappy-java") {
            because("CVE-2023-34454")
            version {
                require("1.1.10.4")
            }
        }
    }
}

tasks {

    extra["snakeyaml.version"] = "2.2"

    named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
        this.archiveFileName.set("app.jar")
    }

    withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "21"
            if (System.getenv("CI") == "true") {
                kotlinOptions.allWarningsAsErrors = true
            }
        }
    }

    named<Jar>("jar") {
        enabled = false
    }

    withType<Test> {
        useJUnitPlatform()
        testLogging {
            events("STANDARD_OUT", "STARTED", "PASSED", "FAILED", "SKIPPED")
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        }
    }
}

detekt {
    config.from("detekt-config.yml")
    buildUponDefaultConfig = true
}
