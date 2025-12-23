plugins {
  kotlin("jvm") version "2.2.21"
  kotlin("plugin.spring") version "2.2.21"
  kotlin("kapt") version "2.2.21"
  id("org.springframework.boot") version "4.0.0"
  id("io.spring.dependency-management") version "1.1.7"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
description = "Demo project for Spring Boot"

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(21)
  }
}

configurations {
  compileOnly {
    extendsFrom(configurations.annotationProcessor.get())
  }
}

repositories {
  mavenCentral()
}

dependencies {
  //JWT
  implementation("io.jsonwebtoken:jjwt-api:0.12.5")
  runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.5")
  runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.5")
//
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive:4.0.0")
  implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
  implementation("org.springframework.boot:spring-boot-starter-liquibase")
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
  implementation("org.springframework:spring-jdbc")
  implementation("tools.jackson.module:jackson-module-kotlin")

  implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:3.0.0")
  // MapStruct
  implementation("org.mapstruct:mapstruct:1.6.3")
  kapt("org.mapstruct:mapstruct-processor:1.6.3")

  // AWS S3
  implementation(platform("software.amazon.awssdk:bom:2.25.11"))
  implementation("software.amazon.awssdk:s3")

  // Image Processing
  implementation("net.coobird:thumbnailator:0.4.20")
  implementation("com.twelvemonkeys.imageio:imageio-webp:3.10.1")

  compileOnly("org.projectlombok:lombok")
  runtimeOnly("org.postgresql:postgresql")
  runtimeOnly("org.postgresql:r2dbc-postgresql")
  annotationProcessor("org.projectlombok:lombok")
  testImplementation("org.springframework.boot:spring-boot-starter-data-r2dbc-test")
  testImplementation("org.springframework.boot:spring-boot-starter-liquibase-test")
  testImplementation("org.springframework.boot:spring-boot-starter-security-test")
  testImplementation("org.springframework.boot:spring-boot-starter-webflux-test")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
  testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
  compilerOptions {
    freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
}
