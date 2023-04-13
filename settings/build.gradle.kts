plugins {
    java
}

dependencies {
    implementation(libs.tomlj)

    testCompileOnly(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
    testImplementation(libs.assertj)
}


tasks {
    test {
        useJUnitPlatform()
    }
}