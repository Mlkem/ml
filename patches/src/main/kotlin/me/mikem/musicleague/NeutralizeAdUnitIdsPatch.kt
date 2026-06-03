package me.mikem.musicleague

import app.morphe.patcher.patch.resourcePatch

private fun neutralizeCaAppPubAdUnitId(value: String): String {
    require(value.contains("/")) {
        "Unsupported AdMob ad unit format: $value"
    }

    return value.mapIndexed { index, char ->
        if (index > "ca-app-pub-".length && char.isDigit()) '0' else char
    }.joinToString("")
}

private fun ByteArray.replaceAscii(search: String, replacement: String): Pair<ByteArray, Int> {
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

private fun ByteArray.neutralizeAllCaAppPubAdUnitIds(): Pair<ByteArray, Int> {
    val output = copyOf()
    val prefix = "ca-app-pub-".toByteArray(Charsets.US_ASCII)
    var count = 0
    var i = 0

    while (i <= output.size - prefix.size) {
        var matched = true

        for (j in prefix.indices) {
            if (output[i + j] != prefix[j]) {
                matched = false
                break
            }
        }

        if (!matched) {
            i++
            continue
        }

        var end = i + prefix.size

        while (end < output.size) {
            val char = output[end].toInt().toChar()
            val allowed = char.isDigit() || char == '/' || char == '-'

            if (!allowed) {
                break
            }

            end++
        }

        val foundBytes = output.copyOfRange(i, end)
        val found = foundBytes.toString(Charsets.US_ASCII)

        if (
            found.startsWith("ca-app-pub-") &&
            found.contains("/") &&
            !found.contains("~") &&
            found.length > "ca-app-pub-".length
        ) {
            val replacement = neutralizeCaAppPubAdUnitId(found).toByteArray(Charsets.US_ASCII)

            if (replacement.size == foundBytes.size) {
                System.arraycopy(replacement, 0, output, i, replacement.size)
                count++
            }
        }

        i = end.coerceAtLeast(i + 1)
    }

    return output to count
}

@Suppress("unused")
val neutralizeAdUnitIdsPatch = resourcePatch(
    name = "Neutralize bundled ad unit IDs",
    description = "Replaces bundled ad unit IDs in the Hermes bundle with same-length non-serving IDs while leaving the AdMob app ID intact.",
    default = true,
) {
    compatibleWith(MUSIC_LEAGUE)

    execute {
        val bundleFile = get("assets/index.android.bundle")
        var data = bundleFile.readBytes()
        var total = 0

        val caAppPubResult = data.neutralizeAllCaAppPubAdUnitIds()
        data = caAppPubResult.first
        total += caAppPubResult.second

        val knownReplacements = mapOf(
            "ca-app-pub-1063041944129884/3965053636" to "ca-app-pub-0000000000000000/0000000000",
            "ca-app-pub-1063041944129884/7616846239" to "ca-app-pub-0000000000000000/0000000000",
            "ca-app-pub-1063041944129884/3274808943" to "ca-app-pub-0000000000000000/0000000000",
            "ca-app-pub-1063041944129884/3869172910" to "ca-app-pub-0000000000000000/0000000000",
            "ca-app-pub-1063041944129884/6060221983" to "ca-app-pub-0000000000000000/0000000000",
            "ca-app-pub-1063041944129884/7068576428" to "ca-app-pub-0000000000000000/0000000000",
            "ca-app-pub-1063041944129884/8003062887" to "ca-app-pub-0000000000000000/0000000000",

            "ca-app-pub-3940256099942544/1033173712" to "ca-app-pub-0000000000000000/0000000000",
            "ca-app-pub-3940256099942544/1044960115" to "ca-app-pub-0000000000000000/0000000000",
            "ca-app-pub-3940256099942544/2247696110" to "ca-app-pub-0000000000000000/0000000000",
            "ca-app-pub-3940256099942544/5224354917" to "ca-app-pub-0000000000000000/0000000000",
            "ca-app-pub-3940256099942544/5354046379" to "ca-app-pub-0000000000000000/0000000000",
            "ca-app-pub-3940256099942544/6300978111" to "ca-app-pub-0000000000000000/0000000000",
            "ca-app-pub-3940256099942544/8691691433" to "ca-app-pub-0000000000000000/0000000000",
            "ca-app-pub-3940256099942544/9214589741" to "ca-app-pub-0000000000000000/0000000000",
            "ca-app-pub-3940256099942544/9257395921" to "ca-app-pub-0000000000000000/0000000000",
        )

        for ((search, replacement) in knownReplacements) {
            val result = data.replaceAscii(search, replacement)
            data = result.first
            total += result.second
        }

        if (total > 0) {
            bundleFile.writeBytes(data)
        }
    }
}
