plugins {
    `java-library`
}

dependencies {
    implementation(project(":api"))
    implementation(libs.spring.autoconfiguration)
    api(libs.spring.kafka)
}