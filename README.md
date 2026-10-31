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
The release workflow generates the full patch catalog here from the built bundle.
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
