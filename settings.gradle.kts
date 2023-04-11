rootProject.name = "reference-car-service-project"

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("junit", "5.8.2")
            version("jackson", "2.14.2")

            library("junit-api", "org.junit.jupiter", "junit-jupiter-api")
                .versionRef("junit")
            library("junit-engine", "org.junit.jupiter", "junit-jupiter-engine")
                .versionRef("junit")
            library("jackson", "com.fasterxml.jackson.core", "jackson-databind").versionRef("jackson")
            library(
                "jackson-datatype-jsr310",
                "com.fasterxml.jackson.datatype",
                "jackson-datatype-jsr310"
            )
                .versionRef("jackson")
            library(
                "jackson-datatype-jdk8",
                "com.fasterxml.jackson.datatype",
                "jackson-datatype-jdk8"
            )
                .versionRef("jackson")

            library("assertj", "org.assertj:assertj-core:3.23.1")
            library("logback", "ch.qos.logback:logback-classic:1.4.6")
            library("tomlj", "org.tomlj:tomlj:1.1.0")
        }
    }
}
