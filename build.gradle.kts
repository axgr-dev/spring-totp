import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("org.springframework.boot") version "2.7.14"
  id("io.spring.dependency-management") version "1.0.15.RELEASE"
  kotlin("jvm") version "1.6.21"
  kotlin("plugin.spring") version "1.6.21"
}

group = "dev.axgr"
version = "0.0.1-SNAPSHOT"

java {
  sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
  mavenCentral()
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.jetbrains.kotlin:kotlin-reflect")

//  Zebra Crossing QR Image Processing Library
  implementation("com.google.zxing:core:3.4.1")
  implementation("com.google.zxing:javase:3.4.1")

//  TOTP Library
  implementation("dev.turingcomplete:kotlin-onetimepassword:2.0.1")

  testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    freeCompilerArgs += "-Xjsr305=strict"
    jvmTarget = "11"
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
}
