plugins {
    java
    `java-test-fixtures`
}

dependencies {
    implementation(project(":api"))

    testFixturesImplementation(project(":api"))
    testFixturesCompileOnly(libs.junit.api)
}