package me.mikem.musicleague

import app.morphe.patcher.patch.resourcePatch
import org.w3c.dom.Element

private const val ANDROID_NS = "http://schemas.android.com/apk/res/android"

private fun Element.androidAttr(name: String): String {
    return getAttributeNS(ANDROID_NS, name).ifBlank { getAttribute("android:$name") }
}

private fun Element.setAndroidAttr(name: String, value: String) {
    if (hasAttributeNS(ANDROID_NS, name)) {
        setAttributeNS(ANDROID_NS, "android:$name", value)
    } else {
        setAttribute("android:$name", value)
    }
}

@Suppress("unused")
val disableExpoUpdatesPatch = resourcePatch(
    name = "Disable Expo updates",
    description = "Prevents Expo OTA updates from overriding patched bundled application code.",
    default = true,
) {
    compatibleWith(MUSIC_LEAGUE)

    execute {
        document("AndroidManifest.xml").use { document ->
            val metaDataNodes = document.getElementsByTagName("meta-data")

            for (i in 0 until metaDataNodes.length) {
                val node = metaDataNodes.item(i)
                if (node !is Element) continue

                when (node.androidAttr("name")) {
                    "expo.modules.updates.ENABLED" -> {
                        node.setAndroidAttr("value", "false")
                    }

                    "expo.modules.updates.EXPO_UPDATES_CHECK_ON_LAUNCH" -> {
                        node.setAndroidAttr("value", "NEVER")
                    }

                    "expo.modules.updates.EXPO_UPDATES_LAUNCH_WAIT_MS" -> {
                        node.setAndroidAttr("value", "0")
                    }

                    "expo.modules.updates.EXPO_UPDATE_URL" -> {
                        // Keep the node present but neutral. Some Expo builds expect the key to exist.
                        node.setAndroidAttr("value", "")
                    }
                }
            }

            // Do not throw if Morphe is run against an already-modified APK or a build where
            // Expo Updates metadata has been stripped. The absence of these keys is already safe.
        }
    }
}
