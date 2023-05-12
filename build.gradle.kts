allprojects {
    group = "com.andersenlab.carservice"
    version = "1.1"
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