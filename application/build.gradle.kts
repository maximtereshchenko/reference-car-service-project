plugins {
    application
}

application {
    mainClass.set("com.andersenlab.carservice.Main")
}

dependencies {
    implementation(project(":api"))
    implementation(project(":domain"))
    implementation(project(":storage"))
    implementation(project(":command-line-interface"))
    implementation(project(":settings"))

    testCompileOnly(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
    testImplementation(libs.assertj)
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