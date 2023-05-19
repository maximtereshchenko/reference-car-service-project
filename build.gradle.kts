allprojects {
    group = "com.andersenlab.carservice"
    version = "2.5"
}

subprojects {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

task("version") {
    doLast {
        println(project.version)
    }
}