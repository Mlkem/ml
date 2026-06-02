package me.mikem.musicleague

import app.morphe.patcher.patch.resourcePatch

@Suppress("unused")
val pinLocalExpoConfigPatch = resourcePatch(
    name = "Pin local Expo config",
    description = "Removes the Expo update URL from the bundled Expo config so the APK prefers local bundled assets.",
    default = true,
) {
    compatibleWith(MUSIC_LEAGUE)

    execute {
        val configFile = get("assets/app.config")
        val original = configFile.readText()

        // Lightweight string edit avoids adding a JSON dependency to the patch bundle.
        val patched = original
            .replace(
                Regex("\"updates\"\\s*:\\s*\\{\\s*\"fallbackToCacheTimeout\"\\s*:\\s*0\\s*,\\s*\"url\"\\s*:\\s*\"https://u\\.expo\\.dev/c0a173c1-8772-4ecc-ab1d-821cd556380d\"\\s*}\\s*,?"),
                "\"updates\": {\"fallbackToCacheTimeout\": 0}"
            )

        if (patched == original) {
            // Do not fail the whole patch if Expo changes app.config formatting in a later build.
            return@execute
        }

        configFile.writeText(patched)
    }
}
