plugins {
    application
}

repositories {
    mavenCentral()
}

application {
    mainClass.set("com.andersenlab.carservice.Main")
}

group = "com.andersenlab.carservice"
version = "1.0"

dependencies {
    implementation(libs.jackson)
    implementation(libs.jackson.datatype.jsr310)
    implementation(libs.jackson.datatype.jdk8)

    testCompileOnly(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
    testImplementation(libs.assertj)
}


tasks {
    named<JavaExec>("run") {
        standardInput = System.`in`
    }
    test {
        useJUnitPlatform()
    }
    compileTestJava {
        options.compilerArgs.add("-parameters")
    }
}