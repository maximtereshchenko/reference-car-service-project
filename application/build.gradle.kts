plugins {
    application
}

application {
    mainClass.set("com.andersenlab.Main")
}

dependencies {
    implementation(project(":api"))
    implementation(project(":domain"))
    implementation(project(":storage"))
    implementation(project(":http-interface"))
    implementation(project(":settings"))

    testCompileOnly(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
    testImplementation(libs.assertj)
    testImplementation(libs.jackson)
    testImplementation(libs.jackson.datatype.jdk8)
    testImplementation(libs.jackson.datatype.jsr310)
    testImplementation(testFixtures(project(":common")))
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