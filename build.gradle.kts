allprojects {
    group = "com.andersenlab.carservice"
    version = "2.3"
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