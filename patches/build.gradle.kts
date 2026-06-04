group = "me.mikem"

patches {
    about {
        name = "Mike's Music League Patches"
        description = "Private Music League patch bundle for Morphe."
        source = "https://github.com/Mlkem/ml"
        author = "Michael MacDonald"
        contact = "na"
        website = "na"
        license = "Private / personal use"
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-parameters")
    }
}

tasks.register("generatePatchesList") {
    description = "Generate patches-list.json for Morphe source indexing."
    group = "morphe"

    dependsOn("build")

    doLast {
        val output = rootProject.layout.projectDirectory.file("patches-list.json").asFile
        val version = project.version.toString()

        output.writeText(
            """
            {
              "version": "$version",
              "patches": [
                {
                  "name": "Disable Expo updates",
                  "description": "Forces the app to use bundled local assets instead of Expo OTA updates."
                },
                {
                  "name": "Pin local Expo config",
                  "description": "Disables Expo update URL/config values in assets/app.config."
                },
                {
                  "name": "Disable ad SDK manifest components",
                  "description": "Removes or disables ad SDK manifest components and ad identifier permissions where safe."
                },
                {
                  "name": "Neutralize bundled ad unit IDs",
                  "description": "Neutralizes bundled AdMob ad unit IDs while leaving the real AdMob app ID intact."
                },
                {
                  "name": "Neutralize premium and reward-ad prompts",
                  "description": "Blanks known premium, rewarded-ad, and ad prompt strings in the Hermes bundle."
                },
                {
                  "name": "Disable ad and promo remote config",
                  "description": "Blackholes bundled ad, reward, paywall, and premium-banner remote config keys."
                },
                {
                  "name": "Hermes hide ad promo surfaces",
                  "description": "Disabled safety stub. Direct Hermes bytecode modification is not enabled."
                }
              ]
            }
            """.trimIndent()
        )
    }
}
