package me.mikem.musicleague

import app.morphe.patcher.patch.resourcePatch

@Suppress("unused")
val pinLocalExpoConfigPatch = resourcePatch(
    name = "Pin local Expo config",
    description = "Neutralizes the Expo update URL in the bundled Expo config so the APK prefers local bundled assets.",
    default = true,
) {
    compatibleWith(MUSIC_LEAGUE)

    execute {
        val configFile = get("assets/app.config")
        val original = configFile.readText()

        val patched = original.replace(
            "https://u.expo.dev/c0a173c1-8772-4ecc-ab1d-821cd556380d",
            ""
        )

        if (patched != original) {
            configFile.writeText(patched)
        }
    }
}
