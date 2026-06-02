# Music League patches compatible with Morphe

Target inspected APK:

- App: Music League
- Package: `com.musicleague.app`
- Version name: `1.4.9`
- Version code: `357`
- Runtime: Expo / React Native
- Main app bundle: `assets/index.android.bundle`
- Bundle type: Hermes JavaScript bytecode, version 96

This starter bundle now contains four patches:

1. `Disable Expo updates` changes the Android manifest so Expo OTA updates do not replace patched local code at launch.
2. `Pin local Expo config` removes the Expo update URL from `assets/app.config` as a second guardrail.
3. `Disable ad SDK manifest components` removes/disables ad SDK manifest components and ad identifier permissions.
4. `Neutralize bundled ad unit IDs` replaces bundled AdMob IDs in the Hermes bundle with same-length non-serving placeholders.

These patches do not modify account access, paid features, licensing, server behavior, or app security controls.

## Why this is the first patch

The APK has Expo updates enabled in `AndroidManifest.xml`:

- `expo.modules.updates.ENABLED=true`
- `EXPO_UPDATES_CHECK_ON_LAUNCH=ALWAYS`
- update URL: `https://u.expo.dev/c0a173c1-8772-4ecc-ab1d-821cd556380d`

Because the real app UI/business logic is in Hermes bytecode, any local bundle patch can be overwritten by Expo OTA unless updates are pinned off first.

## Build

This follows the Morphe patch template pattern. You need JDK 17 and a GitHub token with package-read access for the Morphe GitHub Packages Maven registry.

```bash
./gradlew :patches:buildAndroid
```

Expected output:

```text
patches/build/libs/patches-0.1.0.mpp
```

If you do not have the wrapper yet, create this repo from the official Morphe patches template, then copy the `patches/src/main/kotlin/me/mikem/musicleague` folder and `patches/build.gradle.kts` changes into it.

## Next patch point

Actual Music League behavior is inside `assets/index.android.bundle`, which is Hermes bytecode. For UI/logic changes, the next step is to either:

- replace `assets/index.android.bundle` with a rebuilt Hermes bundle; or
- write a bytecode-level Hermes patch outside Morphe, then package it as an APK-file replacement patch.

Normal Java/Kotlin Morphe bytecode fingerprints will mostly hit Expo, React Native, Google, billing, ads, and SDK wrapper code — not the app's own screen logic.


## Ad-disable approach

The APK uses a mediated ad stack. See `docs/ad-findings.md` for the detected SDKs. The Morphe-side patch is intentionally split into manifest-level SDK disabling plus Hermes-bundle string neutralization. This avoids resizing the Hermes bytecode bundle while still preventing the bundled ad IDs from serving.
