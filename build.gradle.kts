plugins {
    kotlin("jvm") version "2.2.20"
    kotlin("plugin.spring") version "2.2.20"
    kotlin("plugin.jpa") version "2.2.20"
    // Bumping Spring Boot upgrades Spring Framework, Spring Security, and Tomcat transitively
    id("org.springframework.boot") version "3.5.14"
    id("io.spring.dependency-management") version "1.1.7"
    id("net.researchgate.release") version "3.1.0"
    id("jacoco")
}

group = "com.devopswithprashant.service"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

// Override transitive dependency versions to resolve CVEs
dependencyManagement {
    dependencies {
        // Fixes Jackson RCE & async parser bypasses (GHSA-r7wm-3cxj-wff9, CVE-2026-54512, CVE-2026-54513)
        dependency("com.fasterxml.jackson.core:jackson-core:2.21.4")
        dependency("com.fasterxml.jackson.core:jackson-databind:2.21.4")
        dependency("com.fasterxml.jackson.module:jackson-module-kotlin:2.21.4")

        // Fixes PostgreSQL JDBC driver vulnerabilities (CVE-2026-42198, CVE-2026-54291)
        dependency("org.postgresql:postgresql:42.7.12")

        // Fixes Tomcat HTTP/2, Authorization, and Directory Traversal CVEs (CVE-2026-41293, CVE-2025-55752, etc.)
        dependency("org.apache.tomcat.embed:tomcat-embed-core:10.1.55")

        // Fixes Spring Security critical authorization bypass (CVE-2026-22732, CVE-2025-41248)
        dependency("org.springframework.security:spring-security-core:6.5.9")
        dependency("org.springframework.security:spring-security-web:6.5.9")

        // Fixes Spring Core / Expression / WebMVC DoS & XSS issues (CVE-2025-41249, CVE-2026-41850, CVE-2026-41842)
        dependency("org.springframework:spring-core:6.2.19")
        dependency("org.springframework:spring-expression:6.2.19")
        dependency("org.springframework:spring-webmvc:6.2.19")

        dependency("org.springframework.data:spring-data-commons:3.5.12")
    }
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
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
    testImplementation("org.springframework.security:spring-security-test")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform() 
    
    testLogging {
        events("passed", "skipped", "failed")
        
        showExceptions = true
        showCauses = true
        showStackTraces = true
        
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
}

tasks.jacocoTestCoverageVerification {
    dependsOn(tasks.test)
    violationRules {
        rule {
            limit {
                minimum = "0.80".toBigDecimal()
            }
        }
    }
}

tasks.check {
    dependsOn(tasks.jacocoTestCoverageVerification)
}

tasks.named<Jar>("jar") {
    enabled = false // Disables creation of the -plain.jar file
}

release {
    tagTemplate.set("v\${version}")
}