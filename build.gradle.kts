plugins {
	kotlin("jvm") version "2.2.20"
    kotlin("plugin.spring") version "2.2.20"
    kotlin("plugin.jpa") version "2.2.20"
    id("org.springframework.boot") version "3.5.4"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.devopswithprashant.service"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencies {

	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-security")

	implementation("io.jsonwebtoken:jjwt-api:0.12.7")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.7")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.7")

	implementation("org.flywaydb:flyway-core")
	implementation("org.flywaydb:flyway-database-postgresql")

	runtimeOnly("org.postgresql:postgresql")
	
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
