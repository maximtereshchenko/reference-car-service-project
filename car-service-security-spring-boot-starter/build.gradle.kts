plugins {
    java
}

dependencies {
    implementation(project(":api"))
    implementation(libs.spring.autoconfiguration)
    implementation(libs.spring.oauth2.resource.server)
    implementation(libs.spring.security)
}

tasks {
    compileJava {
        options.compilerArgs.add("-parameters")
    }
}