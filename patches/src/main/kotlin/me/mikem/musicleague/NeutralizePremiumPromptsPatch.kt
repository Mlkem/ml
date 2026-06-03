package me.mikem.musicleague

import app.morphe.patcher.patch.resourcePatch
import java.nio.charset.Charset

private fun ByteArray.mlPremiumReplaceBytes(search: ByteArray, replacement: ByteArray): Pair<ByteArray, Int> {
    require(search.size == replacement.size) { "Replacement must be exactly the same byte length." }

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

private fun ByteArray.mlPremiumReplaceEncodedText(search: String, replacement: String, charset: Charset): Pair<ByteArray, Int> {
    val searchBytes = search.toByteArray(charset)
    val replacementBytes = replacement.toByteArray(charset)
    require(searchBytes.size == replacementBytes.size) {
        "Replacement must be exactly the same encoded byte length. Search=$search Replacement=$replacement"
    }
    return mlPremiumReplaceBytes(searchBytes, replacementBytes)
}

private fun mlPremiumBlank(value: String): String = " ".repeat(value.length)

@Suppress("unused")
val neutralizePremiumPromptsPatch = resourcePatch(
    name = "Neutralize premium and reward-ad prompts",
    description = "Removes bundled premium upsell and reward-ad modal/banner wording from the Hermes bundle with same-length replacements.",
    default = true,
) {
    compatibleWith(MUSIC_LEAGUE)

    execute {
        val replacements = listOf(
            "Unlock the Best Music League Experience" to mlPremiumBlank("Unlock the Best Music League Experience"),
            "Play Without Ads for hours, months or all year" to mlPremiumBlank("Play Without Ads for hours, months or all year"),
            "Premium subscriptions turn off all ads, provide league data and early access to new features" to mlPremiumBlank("Premium subscriptions turn off all ads, provide league data and early access to new features"),
            "GO PREMIUM" to mlPremiumBlank("GO PREMIUM"),
            "Go Premium" to mlPremiumBlank("Go Premium"),
            "Enjoy Six Hours with Zero Ads" to mlPremiumBlank("Enjoy Six Hours with Zero Ads"),
            "Six hours of ad-free gameplay. ~90 seconds" to mlPremiumBlank("Six hours of ad-free gameplay. ~90 seconds"),
            "Six hours of ad-free gameplay. ~90 seconds of ads" to mlPremiumBlank("Six hours of ad-free gameplay. ~90 seconds of ads"),
            "Receive hours of ad-free gameplay as thanks for watching ~90 seconds of ads (usually less!)" to mlPremiumBlank("Receive hours of ad-free gameplay as thanks for watching ~90 seconds of ads (usually less!)")
        )

        val bundleFile = get("assets/index.android.bundle")
        var data = bundleFile.readBytes()
        var total = 0

        for ((search, replacement) in replacements) {
            val utf8Result = data.mlPremiumReplaceEncodedText(search, replacement, Charsets.UTF_8)
            data = utf8Result.first
            total += utf8Result.second

            val utf16Result = data.mlPremiumReplaceEncodedText(search, replacement, Charsets.UTF_16LE)
            data = utf16Result.first
            total += utf16Result.second
        }

        if (total > 0) bundleFile.writeBytes(data)
    }
}
