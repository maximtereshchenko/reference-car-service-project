plugins {
    application
}

application {
    mainClass.set("com.andersenlab.kafka.Main")
}

dependencies {
    implementation(libs.spring.kafka)
    implementation(libs.spring.web)
    implementation(libs.spring.actuator)
}