plugins {
    `java-library`
}

dependencies {
    implementation(project(":api"))
    api(libs.spring.web)
}