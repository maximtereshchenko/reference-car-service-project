rootProject.name = "reference-car-service-project"

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("junit", "5.8.2")

            library("junit-api", "org.junit.jupiter", "junit-jupiter-api")
                .versionRef("junit")
            library("junit-engine", "org.junit.jupiter", "junit-jupiter-engine")
                .versionRef("junit")

            library("assertj", "org.assertj:assertj-core:3.23.1")
        }
    }
}
