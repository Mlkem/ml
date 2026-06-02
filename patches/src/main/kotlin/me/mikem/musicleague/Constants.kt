package me.mikem.musicleague

import app.morphe.patcher.data.AppTarget
import app.morphe.patcher.data.Compatibility

val MUSIC_LEAGUE = Compatibility(
    name = "Music League",
    packageName = "com.musicleague.app",
    appIconColor = 0x9E00C4,
    targets = listOf(
        AppTarget(version = "1.4.9"),
    ),
)
