allprojects {
    group = "com.andersenlab.carservice"
    version = "1.0"
}

subprojects {
    repositories {
        mavenCentral()
    }
}

task("version") {
    doLast {
        println(project.version)
    }
}