plugins {
    application
}

application {
    mainClass.set("com.andersenlab.carservice.Main")
}

dependencies {
    implementation(project(":api"))
    implementation(project(":storage"))
    implementation(libs.logback)
    implementation(libs.tomlj)

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