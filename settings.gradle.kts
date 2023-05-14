rootProject.name = "reference-car-service-project"

include("domain")
include("api")
include("on-disk-storage")
include("jdbc-storage")
include("jpa-entities")
include("jpa-storage")
include("command-line-interface")
include("car-service")
include("settings")
include("common")
include("http-interface")
include("car-service-http-spring-boot-starter")
include("car-service-jpa-spring-boot-starter")
include("car-service-on-disk-spring-boot-starter")
include("car-service-apache-kafka-spring-boot-starter")
include("apache-kafka-consumer")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            val junit = version("junit", "5.8.2")
            val jackson = version("jackson", "2.14.2")
            val hibernate = version("hibernate", "6.2.1.Final")
            val spring = version("spring", "3.0.6")
            val testcontainers = version("testcontainers", "1.18.0")

            library("junit-api", "org.junit.jupiter", "junit-jupiter-api")
                .versionRef(junit)
            library("junit-engine", "org.junit.jupiter", "junit-jupiter-engine")
                .versionRef(junit)
            library("jackson", "com.fasterxml.jackson.core", "jackson-databind").versionRef(jackson)
            library(
                "jackson-datatype-jsr310",
                "com.fasterxml.jackson.datatype",
                "jackson-datatype-jsr310"
            )
                .versionRef(jackson)
            library(
                "jackson-datatype-jdk8",
                "com.fasterxml.jackson.datatype",
                "jackson-datatype-jdk8"
            )
                .versionRef(jackson)
            library("hibernate", "org.hibernate.orm", "hibernate-core").versionRef(hibernate)
            library("hibernate-hikari", "org.hibernate.orm", "hibernate-hikaricp")
                .versionRef(hibernate)
            library("spring-web", "org.springframework.boot", "spring-boot-starter-web")
                .versionRef(spring)
            library("spring-autoconfiguration", "org.springframework.boot", "spring-boot-autoconfigure")
                .versionRef(spring)
            library("spring-test", "org.springframework.boot", "spring-boot-starter-test")
                .versionRef(spring)
            library("spring-jpa", "org.springframework.boot", "spring-boot-starter-data-jpa")
                .versionRef(spring)
            library("spring-actuator", "org.springframework.boot", "spring-boot-starter-actuator")
                .versionRef(spring)
            library("spring-kafka", "org.springframework.kafka", "spring-kafka")
                .versionRef(spring)
            library("testcontainers", "org.testcontainers", "testcontainers")
                .versionRef(testcontainers)
            library("testcontainers-postgresql", "org.testcontainers", "postgresql")
                .versionRef(testcontainers)
            library("testcontainers-kafka", "org.testcontainers", "kafka")
                .versionRef(testcontainers)

            library("assertj", "org.assertj:assertj-core:3.23.1")
            library("logback", "ch.qos.logback:logback-classic:1.4.6")
            library("tomlj", "org.tomlj:tomlj:1.1.0")
            library("jetty", "org.eclipse.jetty:jetty-webapp:11.0.14")
            library("h2", "com.h2database:h2:2.1.214")
            library("hikari", "com.zaxxer:HikariCP:5.0.1")
            library("flyway", "org.flywaydb:flyway-core:9.16.3")
            library("jpa-api", "jakarta.persistence:jakarta.persistence-api:3.1.0")
            library("jdbc.postgresql", "org.postgresql:postgresql:42.6.0")
        }
    }
}
