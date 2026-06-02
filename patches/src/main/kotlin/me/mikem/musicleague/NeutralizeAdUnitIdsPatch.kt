package me.mikem.musicleague

import app.morphe.patcher.patch.PatchException
import app.morphe.patcher.patch.resourcePatch

private fun ByteArray.replaceAscii(search: String, replacement: String): Pair<ByteArray, Int> {
    require(search.length == replacement.length) {
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

private fun neutralizeCaAppPubId(id: String): String {
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
            throw PatchException("No bundled AdMob IDs were found in assets/index.android.bundle")
        }

        bundleFile.writeBytes(data)
    }
}
