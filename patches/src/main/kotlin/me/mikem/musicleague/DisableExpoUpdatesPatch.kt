package me.mikem.musicleague

import app.morphe.patcher.patch.PatchException
import app.morphe.patcher.patch.resourcePatch
import org.w3c.dom.Element

@Suppress("unused")
val disableExpoUpdatesPatch = resourcePatch(
    name = "Disable Expo updates",
    description = "Prevents Expo OTA updates from overriding patched bundled application code.",
    default = true,
) {
    compatibleWith(MUSIC_LEAGUE)

    execute {
        val androidNamespace = "http://schemas.android.com/apk/res/android"

        document("AndroidManifest.xml").use { document ->
            val metaDataNodes = document.getElementsByTagName("meta-data")

            var foundUpdatesEnabled = false
            var foundCheckOnLaunch = false

            for (i in 0 until metaDataNodes.length) {
                val node = metaDataNodes.item(i)
                if (node !is Element) continue

                when (node.getAttributeNS(androidNamespace, "name")) {
                    "expo.modules.updates.ENABLED" -> {
                        node.setAttributeNS(androidNamespace, "android:value", "false")
                        foundUpdatesEnabled = true
                    }

                    "expo.modules.updates.EXPO_UPDATES_CHECK_ON_LAUNCH" -> {
                        node.setAttributeNS(androidNamespace, "android:value", "NEVER")
                        foundCheckOnLaunch = true
                    }

                    "expo.modules.updates.EXPO_UPDATES_LAUNCH_WAIT_MS" -> {
                        node.setAttributeNS(androidNamespace, "android:value", "0")
                    }
                }
            }

            if (!foundUpdatesEnabled) {
                throw PatchException("Could not find Expo updates enabled metadata in AndroidManifest.xml")
            }

            if (!foundCheckOnLaunch) {
                throw PatchException("Could not find Expo update check policy metadata in AndroidManifest.xml")
            }
        }
    }
}
