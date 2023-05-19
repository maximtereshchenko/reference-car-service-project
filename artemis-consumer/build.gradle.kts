plugins {
    application
    alias(libs.plugins.graalvm)
    alias(libs.plugins.spring.boot)
}

application {
    mainClass.set("com.andersenlab.artemis.Main")
}

dependencies {
    implementation(libs.spring.artemis)
    implementation(libs.spring.web)
    implementation(libs.spring.actuator)
}