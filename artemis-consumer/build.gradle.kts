plugins {
    application
}

application {
    mainClass.set("com.andersenlab.artemis.Main")
}

dependencies {
    implementation(libs.spring.artemis)
    implementation(libs.spring.web)
    implementation(libs.spring.actuator)
}