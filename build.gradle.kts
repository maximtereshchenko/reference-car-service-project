plugins {
    application
}

repositories {
    mavenCentral()
}

application {
    mainClass.set("com.andersenlab.carservce.Main")
}

group = "com.andersenlab.carservice"
version = "1.0"

dependencies {
    testCompileOnly(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
    testImplementation(libs.assertj)
}

tasks.test {
    useJUnitPlatform()
}