package me.mikem.musicleague

import app.morphe.patcher.patch.PatchException
import app.morphe.patcher.patch.resourcePatch
import org.w3c.dom.Document
import org.w3c.dom.Element

@Suppress("unused")
val disableAdSdkManifestPatch = resourcePatch(
    name = "Disable ad SDK manifest components",
    description = "Disables bundled ad mediation components and removes ad identifier/ad services permissions.",
    default = true,
) {
    compatibleWith(MUSIC_LEAGUE)

    execute {
        val androidNamespace = "http://schemas.android.com/apk/res/android"

        fun Element.androidAttribute(name: String): String = getAttributeNS(androidNamespace, name)

        fun Document.elements(tagName: String): List<Element> {
            val nodes = getElementsByTagName(tagName)
            return buildList {
                for (i in 0 until nodes.length) {
                    val node = nodes.item(i)
                    if (node is Element) add(node)
                }
            }
        }

        val adClassPrefixes = listOf(
            "com.google.android.gms.ads.",
            "com.inmobi.ads.",
            "com.ironsource.",
            "com.unity3d.services.ads.",
            "com.unity3d.ads.",
            "com.moloco.sdk.",
            "com.vungle.ads.",
            "com.bytedance.sdk.openadsdk.",
            "com.facebook.ads.",
            "com.mbridge.msdk.",
        )

        val adPermissions = setOf(
            "com.google.android.gms.permission.AD_ID",
            "android.permission.ACCESS_ADSERVICES_AD_ID",
            "android.permission.ACCESS_ADSERVICES_ATTRIBUTION",
            "android.permission.ACCESS_ADSERVICES_TOPICS",
            "android.permission.ACCESS_ADSERVICES_CUSTOM_AUDIENCE",
        )

        val adMetaValuesToSet = mapOf(
            "com.google.android.gms.ads.APPLICATION_ID" to "ca-app-pub-0000000000000000~0000000000",
            "com.google.android.gms.ads.DELAY_APP_MEASUREMENT_INIT" to "true",
        )

        val adMetaNamesToRemove = setOf(
            "com.google.android.gms.ads.flag.OPTIMIZE_INITIALIZATION",
            "com.google.android.gms.ads.flag.OPTIMIZE_AD_LOADING",
            "com.bytedance.sdk.pangle.version",
            "com.unity3d.services.core.configuration.AdsSdkInitializer",
        )

        val adConsentMetaNamesToFalse = setOf(
            "google_analytics_adid_collection_enabled",
            "google_analytics_default_allow_ad_storage",
            "google_analytics_default_allow_ad_user_data",
            "google_analytics_default_allow_ad_personalization_signals",
        )

        var changed = 0

        document("AndroidManifest.xml").use { document ->
            // Remove Android ad ID and Privacy Sandbox ad-services permissions.
            for (permission in document.elements("uses-permission")) {
                if (permission.androidAttribute("name") in adPermissions) {
                    permission.parentNode.removeChild(permission)
                    changed++
                }
            }

            // Remove optional Android Privacy Sandbox ads extension library.
            for (library in document.elements("uses-library")) {
                if (library.androidAttribute("name") == "android.ext.adservices") {
                    library.parentNode.removeChild(library)
                    changed++
                }
            }

            // Remove ad SDK activities, services, providers, and receivers.
            for (tagName in listOf("activity", "service", "provider", "receiver")) {
                for (component in document.elements(tagName)) {
                    val name = component.androidAttribute("name")
                    if (adClassPrefixes.any { name.startsWith(it) }) {
                        component.parentNode.removeChild(component)
                        changed++
                    }
                }
            }

            // Remove ad-specific manifest metadata, including nested AndroidX Startup metadata.
            for (metaData in document.elements("meta-data")) {
                val name = metaData.androidAttribute("name")
                when {
                    name in adMetaValuesToSet -> {
                        metaData.setAttributeNS(androidNamespace, "android:value", adMetaValuesToSet.getValue(name))
                        changed++
                    }

                    name in adMetaNamesToRemove -> {
                        metaData.parentNode.removeChild(metaData)
                        changed++
                    }

                    name in adConsentMetaNamesToFalse -> {
                        metaData.setAttributeNS(androidNamespace, "android:value", "false")
                        changed++
                    }
                }
            }
        }

        if (changed == 0) {
            throw PatchException("No ad SDK manifest entries were changed. The target APK may not match the inspected build.")
        }
    }
}
