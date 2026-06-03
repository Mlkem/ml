package me.mikem.musicleague

import app.morphe.patcher.patch.PatchException
import app.morphe.patcher.patch.resourcePatch

private const val ML_HBC_HEADER_SIZE = 128
private const val ML_HBC_SMALL_FUNCTION_HEADER_SIZE = 16
private const val ML_HBC_SMALL_STRING_ENTRY_SIZE = 4
private const val ML_HBC_OVERFLOW_STRING_ENTRY_SIZE = 8

private const val ML_HBC_OPCODE_RET = 92
private const val ML_HBC_OPCODE_LOAD_CONST_NULL = 119
private const val ML_HBC_OPCODE_LOAD_CONST_FALSE = 121

private data class MlHermesStringTables(
    val stringCount: Int,
    val smallStringTableOffset: Int,
    val overflowStringTableOffset: Int,
    val stringStorageOffset: Int,
)

private data class MlHermesFunction(
    val index: Int,
    val offset: Int,
    val bytecodeSize: Int,
    val name: String,
)

private fun ByteArray.mlHbcU8(offset: Int): Int = this[offset].toInt() and 0xFF

private fun ByteArray.mlHbcU32(offset: Int): Int {
    return mlHbcU8(offset) or
        (mlHbcU8(offset + 1) shl 8) or
        (mlHbcU8(offset + 2) shl 16) or
        (mlHbcU8(offset + 3) shl 24)
}

private fun ByteArray.mlHbcStringAt(stringId: Int, tables: MlHermesStringTables): String? {
    if (stringId < 0 || stringId >= tables.stringCount) return null

    val entryWord = mlHbcU32(tables.smallStringTableOffset + stringId * ML_HBC_SMALL_STRING_ENTRY_SIZE)
    val isUtf16 = (entryWord and 1) != 0
    var stringOffset = (entryWord ushr 1) and ((1 shl 23) - 1)
    var stringLength = (entryWord ushr 24) and 0xFF

    if (stringLength == 0xFF) {
        val overflowEntryOffset = tables.overflowStringTableOffset + stringOffset * ML_HBC_OVERFLOW_STRING_ENTRY_SIZE
        stringOffset = mlHbcU32(overflowEntryOffset)
        stringLength = mlHbcU32(overflowEntryOffset + 4)
    }

    val encodedLength = if (isUtf16) stringLength * 2 else stringLength
    val start = tables.stringStorageOffset + stringOffset
    val end = start + encodedLength

    if (start < 0 || end > size || start > end) return null

    return if (isUtf16) {
        copyOfRange(start, end).toString(Charsets.UTF_16LE)
    } else {
        copyOfRange(start, end).toString(Charsets.UTF_8)
    }
}

private fun ByteArray.mlHbcPatchFunctionReturnNull(functionOffset: Int) {
    this[functionOffset] = ML_HBC_OPCODE_LOAD_CONST_NULL.toByte()
    this[functionOffset + 1] = 0
    this[functionOffset + 2] = ML_HBC_OPCODE_RET.toByte()
    this[functionOffset + 3] = 0
}

private fun ByteArray.mlHbcPatchFunctionReturnFalse(functionOffset: Int) {
    this[functionOffset] = ML_HBC_OPCODE_LOAD_CONST_FALSE.toByte()
    this[functionOffset + 1] = 0
    this[functionOffset + 2] = ML_HBC_OPCODE_RET.toByte()
    this[functionOffset + 3] = 0
}

private fun ByteArray.mlHbcContainsU16(value: Int, start: Int, end: Int): Boolean {
    val lo = value and 0xFF
    val hi = (value ushr 8) and 0xFF
    var i = start

    while (i + 1 < end) {
        if (mlHbcU8(i) == lo && mlHbcU8(i + 1) == hi) return true
        i++
    }

    return false
}

@Suppress("unused")
val hermesHideAdPromoSurfacesPatch = resourcePatch(
    name = "Hermes hide ad promo surfaces",
    description = "Patches the Hermes bytecode functions that render the remaining premium/reward-ad banners so those components return null and ad visibility hooks return false.",
    default = true,
) {
    compatibleWith(MUSIC_LEAGUE)

    execute {
        val bundleFile = get("assets/index.android.bundle")
        val original = bundleFile.readBytes()

        val magicLow = original.mlHbcU32(0)
        val magicHigh = original.mlHbcU32(4)
        if (magicLow != 0x03BC1FC6 || magicHigh != 0x1F1903C1) {
            throw PatchException("assets/index.android.bundle is not a Hermes bytecode bundle")
        }

        val version = original.mlHbcU32(8)
        if (version != 96) {
            throw PatchException("Unsupported Hermes bytecode version: $version. Expected 96 for Music League 1.4.9")
        }

        val functionCount = original.mlHbcU32(40)
        val stringKindCount = original.mlHbcU32(44)
        val identifierCount = original.mlHbcU32(48)
        val stringCount = original.mlHbcU32(52)
        val overflowStringCount = original.mlHbcU32(56)

        val functionHeadersOffset = ML_HBC_HEADER_SIZE
        val stringKindsOffset = functionHeadersOffset + functionCount * ML_HBC_SMALL_FUNCTION_HEADER_SIZE
        val identifierHashesOffset = stringKindsOffset + stringKindCount * 4
        val smallStringTableOffset = identifierHashesOffset + identifierCount * 4
        val overflowStringTableOffset = smallStringTableOffset + stringCount * ML_HBC_SMALL_STRING_ENTRY_SIZE
        val stringStorageOffset = overflowStringTableOffset + overflowStringCount * ML_HBC_OVERFLOW_STRING_ENTRY_SIZE

        val tables = MlHermesStringTables(
            stringCount = stringCount,
            smallStringTableOffset = smallStringTableOffset,
            overflowStringTableOffset = overflowStringTableOffset,
            stringStorageOffset = stringStorageOffset,
        )

        val returnNullFunctionNames = setOf(
            "AdsCard",
            "AdBanner",
            "AdsSubscriptionModal",
            "AdsSubscriptionPackages",
            "renderPaywall",
            "openPaywall",
        )

        val returnFalseFunctionNames = setOf(
            "useShouldShowAds",
            "useShouldShowBannerAds",
            "useShouldShowMrecAds",
            "useShouldShowPaywall",
        )

        val bannerStringValues = setOf(
            "Unlock the Best Music League Experience",
            "Premium subscriptions turn off all ads, provide league data and early access to new features",
            "🎁 Six Hours with Zero Ads",
            "Enjoy **Six** Hours with **Zero** Ads",
            "Six hours of ad-free gameplay. ~90 seconds of ads",
            "Receive hours of ad-free gameplay as thanks for watching ~90 seconds of ads (usually less!)",
        )

        val bannerStringIds = buildSet {
            for (stringId in 0 until stringCount) {
                val value = original.mlHbcStringAt(stringId, tables) ?: continue
                if (value in bannerStringValues) add(stringId)
            }
        }

        val functions = buildList {
            for (functionIndex in 0 until functionCount) {
                val headerOffset = functionHeadersOffset + functionIndex * ML_HBC_SMALL_FUNCTION_HEADER_SIZE
                if (headerOffset + ML_HBC_SMALL_FUNCTION_HEADER_SIZE > original.size) break

                val word1 = original.mlHbcU32(headerOffset)
                val word2 = original.mlHbcU32(headerOffset + 4)
                val word4 = original.mlHbcU32(headerOffset + 12)

                val flags = (word4 ushr 24) and 0xFF
                val isOverflowedHeader = (flags and 0x20) != 0
                if (isOverflowedHeader) continue

                val functionOffset = word1 and ((1 shl 25) - 1)
                val bytecodeSize = word2 and ((1 shl 15) - 1)
                val functionNameId = (word2 ushr 15) and ((1 shl 17) - 1)

                if (bytecodeSize < 4 || functionOffset < 0 || functionOffset + bytecodeSize > original.size) continue

                val functionName = original.mlHbcStringAt(functionNameId, tables) ?: ""

                add(
                    MlHermesFunction(
                        index = functionIndex,
                        offset = functionOffset,
                        bytecodeSize = bytecodeSize,
                        name = functionName,
                    )
                )
            }
        }

        val patched = original.copyOf()
        val patchedOffsets = mutableSetOf<Int>()
        var patchedCount = 0

        for (function in functions) {
            if (function.name in returnNullFunctionNames && patchedOffsets.add(function.offset)) {
                patched.mlHbcPatchFunctionReturnNull(function.offset)
                patchedCount++
            }

            if (function.name in returnFalseFunctionNames && patchedOffsets.add(function.offset)) {
                patched.mlHbcPatchFunctionReturnFalse(function.offset)
                patchedCount++
            }
        }

        for (function in functions) {
            if (function.bytecodeSize > 1200) continue

            val start = function.offset
            val end = function.offset + function.bytecodeSize
            val containsBannerStringReference = bannerStringIds.any { stringId ->
                original.mlHbcContainsU16(stringId, start, end)
            }

            if (containsBannerStringReference && patchedOffsets.add(function.offset)) {
                patched.mlHbcPatchFunctionReturnNull(function.offset)
                patchedCount++
            }
        }

        if (patchedCount == 0) {
            throw PatchException("No matching Hermes functions were found for the ad/premium banner patch")
        }

        bundleFile.writeBytes(patched)
    }
}
