plugins {
    java
}

dependencies {
    implementation(project(":api"))
    implementation(project(":common"))
    implementation(project(":on-disk-storage"))
    implementation(libs.spring.autoconfiguration)
}