# Mlkem Patches

One Morphe source for Music League, Walmart Canada, Wongnai, Zoom Earth, PushPress,
and the XAPK merge-only utility. The original Music League repository URL is kept
so existing links, history, and subscriptions continue to work.

**[Add Mlkem Patches to Morphe](https://morphe.software/add-source?github=Mlkem/ml&name=Mlkem%20Patches)** ·
[Releases and MPP downloads](https://github.com/Mlkem/ml/releases) ·
[Report a problem](https://github.com/Mlkem/ml/issues)

## Install and update

1. Open the **Add to Morphe** link on Android and confirm adding the source.
   Alternatively add `https://github.com/Mlkem/ml` under **Sources → + → Remote**.
2. For the new combined collection, enable **Pre-release patches** on this source.
   The existing stable release is the older Music League-only bundle until the
   combined collection passes phone testing.
3. Enable **Experimental app versions** for Walmart Canada and PushPress.
4. Select the supported original app APK/XAPK, choose patches, patch, and install.
5. Leave source updates enabled. New published releases arrive through Morphe;
   tap to repatch/install when an app update is available. Downloading MPP files
   manually is optional.

Keep the same Morphe signing key for future updates over a patched installation.
If you previously imported one of these bundles as a **local** source, choose
Mlkem Patches for that app and disable the old source for it to avoid duplicate
patch selections. Your old download is still available as a backup.

## Collection status

| App / utility | Target | Status |
| --- | --- | --- |
| Music League | `com.musicleague.app` 1.4.9 | Existing source migrated; combined build awaits phone testing |
| Walmart Canada | `ca.walmart.ecommerceapp` 26.40 | 1.2.0-beta.1 port; 12 patches; experimental target |
| Wongnai | `com.wongnai.android` 12.11.0 | Version-specific Google ad fix; phone testing pending |
| Zoom Earth | `com.neave.zoomearth` 6.1 | Removes injected LiteAPKS/9MOD popup; requires matching modified APK |
| PushPress Members | `com.pushpress.memberportal` 3.42.0 | CrossFit class selector; experimental target; phone testing pending |
| Merge XAPK only | Universal | Opt-in no-op that uses Morphe's existing merge/sign flow |

The experimental Music League Hermes bytecode patch is **off by default** because
startup behavior needs testing. Other Music League patch defaults are preserved.
Walmart's optional Remove Shop patch and universal Merge XAPK only are also off by default.

## Release channels

- **Beta:** `dev` commits using `feat:` or `fix:` run the build, verify the bundle,
  and automatically publish a GitHub prerelease and updated source metadata.
- **Stable:** only after physical-device tests. Stable publication requires a
  manual Release workflow on main plus a phone-test approval matching the exact
  current sources. See [phone testing](docs/phone-testing.md).
- MPP files, release notes, generated patch metadata and previous releases remain
  available on GitHub. GitHub builds the patch bundle; Morphe patches apps locally.

Selected personal variants are intentionally excluded from this public repository,
including every public branch and release. A separate private repository is required
for private source; a branch in this repository is not private.

## Patch catalog

<!-- PATCHES_START -->
> **[v1.0.0-dev.1](https://github.com/Mlkem/ml/releases/tag/v1.0.0-dev.1)**&nbsp;&nbsp;•&nbsp;&nbsp;`dev`&nbsp;&nbsp;•&nbsp;&nbsp;23 patches total
<details>
<summary>📦 Walmart Canada&nbsp;&nbsp;•&nbsp;&nbsp;12 patches</summary>
<br>

**Package name:** `ca.walmart.ecommerceapp`

**🎯 Supported versions:**

| 🧪&nbsp;26.40 |
| :---: |

| 💊&nbsp;Patch | 📜&nbsp;Description | ⚙️&nbsp;Options |
|----------|----------------|-----------|
| [Aisle-Sorted Checklist — Canada](#aisle-sorted-checklist-canada) | Group remaining checklist items by supplied aisle, in natural order (A2 before A10). Unknown locations last. No floor-plan routing. |  |
| [Clearer Unit Prices — Canada](#clearer-unit-prices-canada) | Show Walmart's supplied price-per-unit in larger bold text on list items, including the shopping checklist. No guessed conversions. |  |
| [Default Lists Tab — Canada](#default-lists-tab-canada) | Open Lists when entering My Items. Walmart Canada 26.40. |  |
| [Find Item for Linked Items — Canada](#find-item-for-linked-items-canada) | Restore Find item on linked shopping-list products so they can be linked to a different product. |  |
| [Personal Price History — Canada](#personal-price-history-canada) | Save observed shopping-list prices locally. Long-press a price for history. Identified stores and sellers have separate histories; starts recording after installation. |  |
| [Promotion Cleanup — Canada](#promotion-cleanup-canada) | Collapse the shopping-list deals promotional banner. Product discounts, expiry conditions, rewards, totals and checkout are preserved. Not an app-wide ad blocker. |  |
| [Quick Scanner — Canada](#quick-scanner-canada) | Add a Scan barcode shortcut to the shopping checklist, opening Walmart's native scanner and permission flow. |  |
| [Remove Shop — Canada](#remove-shop-canada) | Hide Shop from bottom navigation and redirect the default Shop tab to My Items. Explicit other tabs are preserved. |  |
| [Running List Total — Canada](#running-list-total-canada) | Checklist subtotal, remaining and collected amounts from all loaded items, including offscreen rows. Estimated CAD before tax/fees; missing prices are counted and excluded. |  |
| [Sale-Ending Reminders — Canada](#sale-ending-reminders-canada) | Make Walmart's supplied promotion expiry text prominent on list items. In-list reminders only; no invented deadlines or background notifications. |  |
| [Shopping Mode — Canada](#shopping-mode-canada) | Checklist switches for keeping the screen awake, larger checkboxes and hiding collected rows. Show collected restores them without changing your list. |  |
| [Stock Availability — Canada](#stock-availability-canada) | Show Walmart's supplied availability and message below list item names. Unknown stays unknown; purchase limits are never treated as inventory counts. |  |

</details>

<details>
<summary>📦 Music League&nbsp;&nbsp;•&nbsp;&nbsp;7 patches</summary>
<br>

**Package name:** `com.musicleague.app`

**🎯 Supported versions:**

| 1.4.9 |
| :---: |

| 💊&nbsp;Patch | 📜&nbsp;Description | ⚙️&nbsp;Options |
|----------|----------------|-----------|
| [Disable Expo updates and backup restore](#disable-expo-updates-and-backup-restore) | Forces the app to use the bundled local Hermes asset instead of cached Expo OTA updates or restored app data. |  |
| [Disable ad SDK manifest components](#disable-ad-sdk-manifest-components) | Disables bundled ad mediation components and removes ad identifier/ad services permissions. |  |
| [Disable ad and promo remote config](#disable-ad-and-promo-remote-config) | Blackholes ad/paywall/reward feature-flag keys and remote promo payload keys so the app cannot enable ad or upsell surfaces from remote config. |  |
| [Hermes hide ad promo surfaces](#hermes-hide-ad-promo-surfaces) | Experimental Hermes bytecode edit: makes ad-card components return null. May prevent startup; opt in only for testing. |  |
| [Neutralize bundled ad unit IDs and premium banners](#neutralize-bundled-ad-unit-ids-and-premium-banners) | Replaces bundled ad unit IDs and premium/reward banner copy in the Hermes bundle with same-length non-serving/blank values while leaving the AdMob app ID intact. |  |
| [Neutralize premium and reward-ad prompts](#neutralize-premium-and-reward-ad-prompts) | Force-removes the remaining premium/reward banner strings from the bundled Hermes asset. |  |
| [Pin local Expo config](#pin-local-expo-config) | Neutralizes Expo OTA update configuration in the bundled Expo config so the APK prefers local bundled assets. |  |

</details>

<details>
<summary>📦 Wongnai&nbsp;&nbsp;•&nbsp;&nbsp;1 patch</summary>
<br>

**Package name:** `com.wongnai.android`

**🎯 Supported versions:**

| 12.11.0 |
| :---: |

| 💊&nbsp;Patch | 📜&nbsp;Description | ⚙️&nbsp;Options |
|----------|----------------|-----------|
| [Disable Wongnai Google ads](#disable-wongnai-google-ads) | Stops the identified Google banner, native and interstitial ad loaders in Wongnai 12.11.0, and uses its existing photo-ad fallback. |  |

</details>

<details>
<summary>📦 Zoom Earth&nbsp;&nbsp;•&nbsp;&nbsp;1 patch</summary>
<br>

**Package name:** `com.neave.zoomearth`

**🎯 Supported versions:**

| 6.1 |
| :---: |

| 💊&nbsp;Patch | 📜&nbsp;Description | ⚙️&nbsp;Options |
|----------|----------------|-----------|
| [Remove LiteAPKS nag](#remove-liteapks-nag) | Removes the injected LiteAPKS / 9MOD promotional popup from Zoom Earth. |  |

</details>

<details>
<summary>📦 PushPress Members&nbsp;&nbsp;•&nbsp;&nbsp;1 patch</summary>
<br>

**Package name:** `com.pushpress.memberportal`

**🎯 Supported versions:**

| 🧪&nbsp;3.42.0 |
| :---: |

| 💊&nbsp;Patch | 📜&nbsp;Description | ⚙️&nbsp;Options |
|----------|----------------|-----------|
| [Workouts: force CrossFit class](#workouts-force-crossfit-class) | On the Workouts page, automatically selects CrossFit class from the Class type dropdown. |  |

</details>

<details>
<summary>🌐 Universal&nbsp;&nbsp;•&nbsp;&nbsp;1 patch</summary>
<br>

| 💊&nbsp;Patch | 📜&nbsp;Description | ⚙️&nbsp;Options |
|----------|----------------|-----------|
| [Merge XAPK only](#merge-xapk-only) | Makes no app-code or resource edits. Morphe merges APK components and signs the result with your key. The original signature is not preserved. Separate OBB/game data is not installed. |  |

</details>

<!-- PATCHES_END -->

## Development

Use JDK 21 and the included Gradle wrapper. Set `GITHUB_ACTOR` and `GITHUB_TOKEN`
with access to Morphe's package registry, or use local `gpr.user` / `gpr.key` Gradle
properties. Never commit credentials. GitHub Actions supplies its own scoped token.

```sh
./gradlew :patches:buildAndroid :patches:generatePatchesList
python3 scripts/verify_bundle.py
```

Output: `patches/build/libs/patches-<version>.mpp`.
The alternative `scripts/build_offline.py --help` compiles against supplied official
Morphe Desktop, Kotlin, Android and R8 jars for local verification.

App patch code lives under `patches/src/main/`; injected code is under `extensions/`.
See [maintenance](docs/maintenance.md), [source provenance](docs/provenance.md), and
[app notes](docs/apps/). `sign/` is an existing unrelated page retained unchanged.

## License

GPL-3.0 for the combined collection and template-derived infrastructure. The Wongnai
component retains its MIT notice. Walmart upstream attribution is preserved in
[its notice](docs/apps/walmart-NOTICE). This is an independent community project,
not an official Morphe or app-vendor release.
