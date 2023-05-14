allprojects {
    group = "com.andersenlab.carservice"
    version = "2.2"
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