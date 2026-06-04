package me.mikem.musicleague

import app.morphe.patcher.patch.resourcePatch

@Suppress("unused")
val hermesHideAdPromoSurfacesPatch = resourcePatch(
    name = "Hermes hide ad promo surfaces",
    description = "Disabled safety stub. Direct Hermes bytecode modification caused the app to hang on the intro/loading screen.",
    default = false,
) {
    compatibleWith(MUSIC_LEAGUE)

    execute {
        // Intentionally disabled.
        // Do not modify assets/index.android.bundle here.
    }
}
