package me.mikem.musicleague

import app.morphe.patcher.patch.resourcePatch

private fun ByteArray.replaceAsciiText(search: String, replacement: String): Pair<ByteArray, Int> {
    require(search.toByteArray(Charsets.US_ASCII).size == replacement.toByteArray(Charsets.US_ASCII).size) {
        "Replacement must be exactly the same byte length. Search=$search Replacement=$replacement"
    }

    val needle = search.toByteArray(Charsets.US_ASCII)
    val value = replacement.toByteArray(Charsets.US_ASCII)
    val output = copyOf()
    var count = 0
    var i = 0

    while (i <= output.size - needle.size) {
        var matched = true

        for (j in needle.indices) {
            if (output[i + j] != needle[j]) {
                matched = false
                break
            }
        }

        if (matched) {
            System.arraycopy(value, 0, output, i, value.size)
            count++
            i += needle.size
        } else {
            i++
        }
    }

    return output to count
}

private fun blankSameLength(value: String): String = " ".repeat(value.length)

@Suppress("unused")
val neutralizePremiumPromptsPatch = resourcePatch(
    name = "Neutralize premium and reward-ad prompts",
    description = "Removes bundled premium upsell and reward-ad modal/banner wording from the Hermes bundle with same-length replacements.",
    default = true,
) {
    compatibleWith(MUSIC_LEAGUE)

    execute {
        val replacements = listOf(
            // Home premium banner.
            "Unlock the Best Music League Experience" to blankSameLength("Unlock the Best Music League Experience"),
            "Play Without Ads for hours, months or all year" to blankSameLength("Play Without Ads for hours, months or all year"),
            "Premium subscriptions turn off all ads, provide league data and early access to new features" to blankSameLength("Premium subscriptions turn off all ads, provide league data and early access to new features"),
            "Go Premium" to blankSameLength("Go Premium"),
            "GO PREMIUM" to blankSameLength("GO PREMIUM"),

            // Rewarded-ad / subscription popup.
            "Six Hours with Zero" to blankSameLength("Six Hours with Zero"),
            "Months with Zero Ads" to blankSameLength("Months with Zero Ads"),
            "Receive hours of ad-free gameplay as thanks for watching ~90 seconds of ads (usually less!)" to blankSameLength("Receive hours of ad-free gameplay as thanks for watching ~90 seconds of ads (usually less!)"),
            "Six hours of ad-free gameplay. ~90 seconds of ads" to blankSameLength("Six hours of ad-free gameplay. ~90 seconds of ads"),
            "hours of uninterrupted, ad-free gameplay." to blankSameLength("hours of uninterrupted, ad-free gameplay."),
            "Reward granted! Enjoy **Six** Hours with **Zero** Ads" to blankSameLength("Reward granted! Enjoy **Six** Hours with **Zero** Ads"),

            // Buttons.
            "Watch Rewarded Ads" to blankSameLength("Watch Rewarded Ads"),
            "WATCH REWARDED ADS" to blankSameLength("WATCH REWARDED ADS"),
            "WATCH ADS WITHOUT REWARD" to blankSameLength("WATCH ADS WITHOUT REWARD"),
            "WATCH ADS without REWARD" to blankSameLength("WATCH ADS without REWARD"),
            "WATCH ADS FOR REWARD" to blankSameLength("WATCH ADS FOR REWARD"),
            "WATCH ADS for REWARD" to blankSameLength("WATCH ADS for REWARD"),
            "Subscribe NOW" to blankSameLength("Subscribe NOW"),
            "SUBSCRIBE" to blankSameLength("SUBSCRIBE"),

            // Subscription copy.
            "Unlock the Best Music League" to blankSameLength("Unlock the Best Music League"),
            "Zero ads" to blankSameLength("Zero ads"),
            "Download league-by-league data" to blankSameLength("Download league-by-league data"),
            "Early access to new features" to blankSameLength("Early access to new features"),
            "Monthly" to blankSameLength("Monthly"),
            "Annual" to blankSameLength("Annual"),
            "Save 31%" to blankSameLength("Save 31%"),

            // Possible adjacent keys / labels.
            "Zero ads_card_description" to blankSameLength("Zero ads_card_description"),
            "subscription_bullet_1" to blankSameLength("subscription_bullet_1"),
            "subscription_bullet_2" to blankSameLength("subscription_bullet_2"),
            "subscription_bullet_3" to blankSameLength("subscription_bullet_3"),
        )

        val bundleFile = get("assets/index.android.bundle")
        var data = bundleFile.readBytes()
        var total = 0

        for ((search, replacement) in replacements) {
            val result = data.replaceAsciiText(search, replacement)
            data = result.first
            total += result.second
        }

        if (total > 0) {
            bundleFile.writeBytes(data)
        }
    }
}private fun neutralizeCaAppPubId(id: String): String {
    val separatorIndex = id.indexOfAny(charArrayOf('/', '~'))
    require(separatorIndex > 0) { "Unsupported AdMob identifier format: $id" }

    val prefix = "ca-app-pub-"
    require(id.startsWith(prefix)) { "Unsupported AdMob identifier format: $id" }

    val publisherIdLength = separatorIndex - prefix.length
    val unitIdLength = id.length - separatorIndex - 1
    val separator = id[separatorIndex]

    return prefix + "0".repeat(publisherIdLength) + separator + "0".repeat(unitIdLength)
}

@Suppress("unused")
val neutralizeAdUnitIdsPatch = resourcePatch(
    name = "Neutralize bundled ad unit IDs",
    description = "Replaces real and test AdMob app/ad unit IDs in the Hermes bundle with same-length non-serving IDs.",
    default = true,
) {
    compatibleWith(MUSIC_LEAGUE)

    execute {
        val ids = listOf(
            "ca-app-pub-1063041944129884~3179051622",
            "ca-app-pub-1063041944129884/13552566871378",
            "ca-app-pub-1063041944129884/27507362022",
            "ca-app-pub-1063041944129884/3274808943",
            "ca-app-pub-1063041944129884/3869172910",
            "ca-app-pub-1063041944129884/3965053636",
            "ca-app-pub-1063041944129884/6060221983",
            "ca-app-pub-1063041944129884/7068576428",
            "ca-app-pub-1063041944129884/7453792378",
            "ca-app-pub-1063041944129884/7616846239",
            "ca-app-pub-1063041944129884/8003062887",
            "ca-app-pub-3940256099942544/1033173712",
            "ca-app-pub-3940256099942544/1044960115",
            "ca-app-pub-3940256099942544/2247696110",
            "ca-app-pub-3940256099942544/5224354917",
            "ca-app-pub-3940256099942544/5354046379",
            "ca-app-pub-3940256099942544/6300978111",
            "ca-app-pub-3940256099942544/8691691433",
            "ca-app-pub-3940256099942544/9214589741",
            "ca-app-pub-3940256099942544/9257395921",
        )

        val bundleFile = get("assets/index.android.bundle")
        var data = bundleFile.readBytes()
        var replacements = 0

        for (id in ids) {
            val replacement = neutralizeCaAppPubId(id)
            val result = data.replaceAscii(id, replacement)
            data = result.first
            replacements += result.second
        }

        if (replacements == 0) {
            // Already patched, or this build stores the ad IDs elsewhere.
            return@execute
        }

        bundleFile.writeBytes(data)
    }
}
