plugins {
    application
}

application {
    mainClass.set("com.andersenlab.Main")
}

dependencies {
    implementation(project(":api"))
    implementation(project(":domain"))
    implementation(project(":jdbc-storage"))
    implementation(project(":jpa-storage"))
    implementation(project(":common"))
    implementation(project(":http-interface"))
    implementation(project(":settings"))
    implementation(libs.h2)
    implementation(libs.hikari)
    implementation(libs.flyway)

    testCompileOnly(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
    testImplementation(libs.assertj)
    testImplementation(libs.jackson)
    testImplementation(libs.jackson.datatype.jdk8)
    testImplementation(libs.jackson.datatype.jsr310)
    testImplementation(testFixtures(project(":common")))
    testImplementation(testFixtures(project(":settings")))
}

tasks {
    test {
        useJUnitPlatform()
    }
    compileTestJava {
        options.compilerArgs.add("-parameters")
    }
}