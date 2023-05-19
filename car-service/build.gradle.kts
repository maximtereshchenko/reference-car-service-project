plugins {
    application
    alias(libs.plugins.graalvm)
    alias(libs.plugins.spring.boot)
}

application {
    mainClass.set("com.andersenlab.Main")
}

dependencies {
    implementation(project(":api"))
    implementation(project(":domain"))
    implementation(libs.flyway)
    implementation(libs.spring.autoconfiguration)
    implementation(project(":car-service-http-spring-boot-starter"))
    implementation(project(":car-service-jpa-spring-boot-starter"))
    implementation(libs.jdbc.postgresql)
    implementation(libs.spring.actuator)
    implementation(project(":car-service-apache-kafka-spring-boot-starter"))
    implementation(project(":car-service-artemis-spring-boot-starter"))
    implementation(project(":car-service-security-spring-boot-starter"))

    testCompileOnly(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
    testImplementation(libs.assertj)
    testImplementation(libs.spring.test)
    testImplementation(testFixtures(project(":common")))
    testImplementation(libs.testcontainers)
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.testcontainers.kafka)
}

tasks {
    test {
        useJUnitPlatform()
    }
    compileTestJava {
        options.compilerArgs.add("-parameters")
    }
}