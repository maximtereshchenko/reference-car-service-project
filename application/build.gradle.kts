plugins {
    application
}

application {
    mainClass.set("com.andersenlab.Main")
}

dependencies {
    implementation(project(":api"))
    implementation(project(":domain"))
    implementation(libs.h2)
    implementation(libs.flyway)
    implementation(libs.spring.autoconfiguration)
    implementation(project(":car-service-http-spring-boot-starter"))
    implementation(project(":car-service-jpa-spring-boot-starter"))

    testCompileOnly(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
    testImplementation(libs.assertj)
    testImplementation(libs.spring.test)
    testImplementation(testFixtures(project(":common")))
}

tasks {
    test {
        useJUnitPlatform()
    }
    compileTestJava {
        options.compilerArgs.add("-parameters")
    }
}