package me.mikem.musicleague

import app.morphe.patcher.patch.resourcePatch
import java.nio.charset.Charset

private fun ByteArray.replaceBytes(search: ByteArray, replacement: ByteArray): Pair<ByteArray, Int> {
    require(search.size == replacement.size) {
        "Replacement must be exactly the same byte length."
    }

    val output = copyOf()
    var count = 0
    var i = 0

    while (i <= output.size - search.size) {
        var matched = true

        for (j in search.indices) {
            if (output[i + j] != search[j]) {
                matched = false
                break
            }
        }

        if (matched) {
            System.arraycopy(replacement, 0, output, i, replacement.size)
            count++
            i += search.size
        } else {
            i++
        }
    }

    return output to count
}

private fun ByteArray.replaceEncodedText(
    search: String,
    replacement: String,
    charset: Charset,
): Pair<ByteArray, Int> {
    val searchBytes = search.toByteArray(charset)
    val replacementBytes = replacement.toByteArray(charset)

    require(searchBytes.size == replacementBytes.size) {
        "Replacement must be exactly the same encoded byte length. Search=$search Replacement=$replacement"
    }

    return replaceBytes(searchBytes, replacementBytes)
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
            // Main screen premium banner.
            "Unlock the Best Music League Experience" to blankSameLength("Unlock the Best Music League Experience"),
            "Unlock the Best Music League" to blankSameLength("Unlock the Best Music League"),
            "Play Without Ads for hours, months or all year" to blankSameLength("Play Without Ads for hours, months or all year"),
            "Premium subscriptions turn off all ads, provide league data and early access to new features" to blankSameLength("Premium subscriptions turn off all ads, provide league data and early access to new features"),
            "GO PREMIUM" to blankSameLength("GO PREMIUM"),
            "Go Premium" to blankSameLength("Go Premium"),

            // Six-hour reward banner / modal.
            "Enjoy Six Hours with Zero Ads" to blankSameLength("Enjoy Six Hours with Zero Ads"),
            "Enjoy six hours with zero ads" to blankSameLength("Enjoy six hours with zero ads"),
            "enjoy six hours with zero ads" to blankSameLength("enjoy six hours with zero ads"),
            "Six Hours with Zero Ads" to blankSameLength("Six Hours with Zero Ads"),
            "six hours with zero ads" to blankSameLength("six hours with zero ads"),
            "Six Hours with Zero" to blankSameLength("Six Hours with Zero"),
            "Zero Ads" to blankSameLength("Zero Ads"),
            "zero ads" to blankSameLength("zero ads"),

            // Rewarded-ad popup.
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

            // Subscription bullets.
            "Zero ads" to blankSameLength("Zero ads"),
            "Download league-by-league data" to blankSameLength("Download league-by-league data"),
            "Early access to new features" to blankSameLength("Early access to new features"),
            "Monthly" to blankSameLength("Monthly"),
            "Annual" to blankSameLength("Annual"),
            "Save 31%" to blankSameLength("Save 31%"),

            // Possible adjacent labels / keys.
            "Zero ads_card_description" to blankSameLength("Zero ads_card_description"),
            "subscription_bullet_1" to blankSameLength("subscription_bullet_1"),
            "subscription_bullet_2" to blankSameLength("subscription_bullet_2"),
            "subscription_bullet_3" to blankSameLength("subscription_bullet_3"),
        )

        val bundleFile = get("assets/index.android.bundle")
        var data = bundleFile.readBytes()
        var total = 0

        for ((search, replacement) in replacements) {
            val utf8Result = data.replaceEncodedText(search, replacement, Charsets.UTF_8)
            data = utf8Result.first
            total += utf8Result.second

            val utf16Result = data.replaceEncodedText(search, replacement, Charsets.UTF_16LE)
            data = utf16Result.first
            total += utf16Result.second
        }

        if (total > 0) {
            bundleFile.writeBytes(data)
        }
    }
}
