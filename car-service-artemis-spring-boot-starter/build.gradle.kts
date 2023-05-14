plugins {
    `java-library`
}

dependencies {
    implementation(project(":api"))
    implementation(libs.spring.autoconfiguration)
    implementation(libs.spring.artemis)
    api(libs.spring.jms)
    api(libs.jms.api)
}