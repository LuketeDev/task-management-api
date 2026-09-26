plugins {
	java
	id("jacoco")
	id("com.github.spotbugs") version "6.5.11"
	id("org.springframework.boot") version "4.1.1"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.owasp.dependencycheck") version "13.0.0"
		// id("io.github.ben-manes.versions.settings") version "0.64.0"
}

group = "com.lukete"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-flyway")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	implementation("org.apache.tomcat.embed:tomcat-embed-core:11.0.26")
	implementation("org.apache.tomcat.embed:tomcat-embed-websocket:11.0.26")
	implementation("org.flywaydb:flyway-database-postgresql")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.1.1")
	compileOnly("org.projectlombok:lombok")
	runtimeOnly("org.postgresql:postgresql")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
	testImplementation("org.springframework.boot:spring-boot-starter-flyway-test")
	testImplementation("org.springframework.boot:spring-boot-starter-validation-test")
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.testcontainers:testcontainers-junit-jupiter")
	testImplementation("org.testcontainers:testcontainers-postgresql")
	testCompileOnly("org.projectlombok:lombok")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testAnnotationProcessor("org.projectlombok:lombok")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
}

tasks.named<com.github.spotbugs.snom.SpotBugsTask>("spotbugsMain") {
    reports {
        create("html") {
            required = true
        }
    }
}



tasks.named<com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask>("dependencyUpdates") {
  revision = "release"
  outputFormatter = "json"
}

dependencyCheck {
    nvd {
        apiKey = System.getenv("NVD_API_KEY")
    }
}

// tasks.register('dependencyCheckRelease', org.owasp.dependencycheck.gradle.tasks.Analyze) {
//     dependencyCheck {
//         failBuildOnCVSS = 9.0
//     }
// }

// tasks.register('dependencyCheckCI', org.owasp.dependencycheck.gradle.tasks.Analyze) {
//     dependencyCheck {
//         failBuildOnCVSS = 3.0
//     }
// }