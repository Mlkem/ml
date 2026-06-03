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

private fun Element.removeAndroidAttr(name: String) {
    if (hasAttributeNS(ANDROID_NS, name)) {
        removeAttributeNS(ANDROID_NS, name)
    }
    if (hasAttribute("android:$name")) {
        removeAttribute("android:$name")
    }
}

@Suppress("unused")
val disableExpoUpdatesPatch = resourcePatch(
    name = "Disable Expo updates and backup restore",
    description = "Forces the app to use the bundled local Hermes asset instead of cached Expo OTA updates or restored app data.",
    default = true,
) {
    compatibleWith(MUSIC_LEAGUE)

    execute {
        document("AndroidManifest.xml").use { document ->
            val applications = document.getElementsByTagName("application")

            if (applications.length > 0) {
                val application = applications.item(0)

                if (application is Element) {
                    application.setAndroidAttr("allowBackup", "false")
                    application.setAndroidAttr("fullBackupOnly", "false")
                    application.removeAndroidAttr("fullBackupContent")
                    application.removeAndroidAttr("dataExtractionRules")
                }
            }

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
                        node.setAndroidAttr("value", "")
                    }

                    "expo.modules.updates.UPDATES_CONFIGURATION_REQUEST_HEADERS_KEY" -> {
                        node.setAndroidAttr("value", "{}")
                    }
                }
            }
        }
    }
}
