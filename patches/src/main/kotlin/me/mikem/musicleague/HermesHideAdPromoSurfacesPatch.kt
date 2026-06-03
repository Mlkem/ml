package me.mikem.musicleague

import app.morphe.patcher.patch.resourcePatch

@Suppress("unused")
val hermesHideAdPromoSurfacesPatch = resourcePatch(
    name = "Hermes hide ad promo surfaces",
    description = "Disabled safety stub. The direct Hermes bytecode patch was removed because it can break app startup.",
    default = false,
) {
    compatibleWith(MUSIC_LEAGUE)

    execute {
        // Intentionally disabled.
        // Do not modify assets/index.android.bundle here.
    }
}
