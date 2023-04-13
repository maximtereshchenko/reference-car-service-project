plugins {
    java
}

dependencies {
    implementation(project(":api"))
    implementation(project(":common"))
    implementation(libs.jackson)
    implementation(libs.jackson.datatype.jsr310)
    implementation(libs.jackson.datatype.jdk8)
    implementation(libs.logback)

    testCompileOnly(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
    testImplementation(libs.assertj)
}


tasks {
    test {
        useJUnitPlatform()
    }
}