plugins {
    java
}

dependencies {
    implementation(project(":api"))
    implementation(libs.logback)
    implementation(libs.jackson)
    implementation(libs.jackson.datatype.jdk8)
    implementation(libs.jackson.datatype.jsr310)
    implementation(libs.jetty)
}