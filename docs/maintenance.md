# Maintenance

## Add or update an app

Work on dev. Keep each app in its own package and extensions module. Update the
package/version compatibility, docs, tests and `scripts/verify_bundle.py` app set
when adding a new app. Run the build and verification before pushing a `feat:` or
`fix:` commit. Never combine prebuilt MPP archives by concatenating ZIP contents;
build all patch classes and their Android discovery DEX together.

Walmart code was imported from the saved 1.2.0-beta.1 source snapshot. Ongoing
Walmart development is separate: bring subsequent reviewed Kotlin, Java extension,
and test changes into this repository; do not replace live work elsewhere.

## Automatic publishing

The release workflow is adapted from Morphe's official template. Semantic release
owns `patches-bundle.json`, `patches-list.json`, version numbers, changelog and release
assets. Retain its tags and generated commits. Only dev auto-publishes prereleases.
main is built by CI, but publishing stable requires the manual phone-tested flow.

The main source metadata continues to point to the original stable Music League
release until a combined stable version is approved. Beta metadata lives on dev.
A bootstrap correction fixes the legacy stable URL, whose tag is `ML-Morphe`.

## Private variants

Do not add private patch code, account identifiers, credentials, personal data,
APK inputs, or signing keys to any branch or release of this public repository.
Keep private variants in a separately private repository. Its access/update method
must be tested separately; this public Add link grants no access to private patches.

## Release signing

The official template supports `./gradlew generateSigningKey`. Commit only the
public key and store the private key as the GitHub Actions `MORPHE_SIGNING_KEY`
secret. Release signing is optional and is not configured by this consolidation.
GitHub build provenance is generated for releases. Do not confuse it with a Morphe
bundle signature or with the phone's APK signing key.
