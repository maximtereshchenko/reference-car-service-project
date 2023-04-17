plugins {
    java
    `java-test-fixtures`
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