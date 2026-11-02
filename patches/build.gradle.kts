group = "ca.mlkem.patches"

// Match Kotlin's JVM 11 target when compiling Java patch entry points on JDK 21.
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
tasks.withType<JavaCompile>().configureEach {
    options.release.set(11)
}

patches {
    about {
        name = "Mlkem Patches"
        description = "Community patches for Music League, Walmart Canada, Wongnai, Zoom Earth, PushPress, and XAPK merging."
        source = "https://github.com/Mlkem/ml"
        author = "Mlkem"
        contact = "https://github.com/Mlkem/ml/issues"
        website = "https://github.com/Mlkem/ml"
        license = "GPLv3"
    }
}

// Separate configuration so gson is available at runtime for the
// generatePatchesList task but never bundled into the APK.
val patchListGeneratorClasspath = configurations.create("patchListGeneratorClasspath")

dependencies {
    compileOnly(libs.gson)
    patchListGeneratorClasspath(libs.gson)
}

tasks {
    register<JavaExec>("generatePatchesList") {
        description = "Build patch with patch list"

        dependsOn(build)

        classpath = sourceSets["main"].runtimeClasspath + patchListGeneratorClasspath
        mainClass.set("util.PatchListGeneratorKt")
    }

    // Used by gradle-semantic-release-plugin.
    publish {
        dependsOn("generatePatchesList")
    }
}
