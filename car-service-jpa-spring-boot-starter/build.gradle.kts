plugins {
    `java-library`
}

dependencies {
    implementation(project(":api"))
    implementation(project(":jpa-entities"))
    implementation(libs.spring.autoconfiguration)
    implementation(libs.spring.jpa)
    implementation(libs.jpa.api)
}