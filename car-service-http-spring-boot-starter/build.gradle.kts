plugins {
    `java-library`
}

dependencies {
    implementation(project(":api"))
    api(libs.spring.web)
}

tasks {
    compileJava {
        options.compilerArgs.add("-parameters")
    }
}