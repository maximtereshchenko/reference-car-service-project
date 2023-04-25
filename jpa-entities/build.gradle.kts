plugins {
    java
}

dependencies {
    implementation(project(":api"))
    compileOnly(libs.jpa.api)
}