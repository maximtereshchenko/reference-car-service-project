allprojects {
    group = "com.andersenlab.carservice"
    version = "2.4"
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