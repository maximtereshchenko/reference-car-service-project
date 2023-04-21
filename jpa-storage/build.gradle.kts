plugins {
    java
}

dependencies {
    implementation(project(":api"))
    implementation(libs.hikari)
    implementation(libs.hibernate)
    implementation(libs.hibernate.hikari)
}