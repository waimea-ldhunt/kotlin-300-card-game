plugins {
    kotlin("jvm") version "2.0.0"
    application
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass = "MainKt"
}

dependencies {
    implementation("com.formdev:flatlaf:3.6.2")
    implementation("com.formdev:flatlaf-intellij-themes:3.6.2")
}


//------------------------------------------------------------------------
// Packaging

tasks.jar {
    archiveVersion = ""
    manifest {
        attributes["Main-Class"] = "MainKt"
        attributes["Add-Opens"] = "java.base/java.lang=ALL-UNNAMED"
    }
    from(configurations.runtimeClasspath.get().map {
        if (it.isDirectory) it else zipTree(it)
    })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.register<Exec>("packageApp") {
    group = "build"
    description = "Package app as a self-contained executable"
    dependsOn("jar")
    doFirst {
        delete("build/package")
    }
    commandLine(
        "jpackage",
        "--input", "build/libs",
        "--main-jar", "${rootProject.name}.jar",
        "--main-class", "MainKt",
        "--type", "app-image",
        "--name", rootProject.name,
        "--dest", "build/package",
        "--java-options", "--enable-native-access=ALL-UNNAMED",
    )
}