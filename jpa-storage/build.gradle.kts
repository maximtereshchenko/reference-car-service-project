plugins {
    java
}

dependencies {
    implementation(project(":api"))
    implementation(project(":jpa-entities"))
    implementation(libs.hikari)
    implementation(libs.hibernate)
    implementation(libs.hibernate.hikari)
}