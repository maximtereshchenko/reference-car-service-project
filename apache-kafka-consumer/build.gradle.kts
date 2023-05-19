plugins {
    application
    alias(libs.plugins.graalvm)
    alias(libs.plugins.spring.boot)
}

application {
    mainClass.set("com.andersenlab.kafka.Main")
}

dependencies {
    implementation(libs.spring.kafka)
    implementation(libs.spring.web)
    implementation(libs.spring.actuator)
}