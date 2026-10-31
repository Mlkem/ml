# Walmart Canada Patch Pack — 1.2.0-beta.1

Targets **Walmart Canada 26.40**, package `ca.walmart.ecommerceapp`. Requires a Morphe release supporting Patcher 2.1.0. Import the MPP as a local patch source in Morphe Manager, select the original Canadian app APK/XAPK, and select the patches you want. This file is a patch bundle, not an installable APK. Use a consistent Morphe signing key for subsequent patched updates.

## Included

| Patch | Default | Behaviour |
|---|---|---|
| Default Lists Tab — Canada | On | Opens Lists from My Items when the pager is at its default first tab and has more than one page. |
| Find Item for Linked Items — Canada | On | Restores the native Find item action for linked products, using Canada's action enum and resource IDs. |
| Stock Availability — Canada | On | Adds the availability status and message already supplied by Walmart to shopping-list item titles. It does not obtain privileged inventory data or infer physical inventory counts from purchase limits. Unknown status is explicitly shown as unknown. English and French labels. |
| Remove Shop — Canada | Off | Removes Shop from the navigation map when My Items exists and redirects the default Shop tab to My Items. Other tab selections remain intact. Enable this for the closest match to the original bundle. |
| Personal Price History — Canada | On | Records visible shopping-list prices locally. Long-press a price to see its history or clear it. Price drops/increases are shown against the previous distinct observed price. |
| Shopping Mode — Canada | On | Adds checklist switches for screen-awake mode and showing collected items. Enlarges checkboxes while enabled. Collected rows collapse by default, and can be restored with the switch. |
| Clearer Unit Prices — Canada | On | Enlarges and bolds Walmart's supplied unit-price text, including in the checklist where the native app hides it. Does not guess missing unit prices. |
| Aisle-Sorted Checklist — Canada | On | Groups remaining checklist items using supplied aisle locations, sorted naturally (A2 before A10). Unknown locations last. Preserves item identity, quantity and checked state. |
| Running List Total — Canada | On | Estimated subtotal, remaining and collected values for every loaded checklist item, including offscreen rows. Shows how many unpriced items were excluded. |
| Quick Scanner — Canada | On | Adds a Scan barcode button to the checklist. Opens the existing Walmart scanner and its standard camera permission flow. |
| Promotion Cleanup — Canada | On | Collapses the deals promotional banner in shopping lists. Product discounts, conditions, rewards and checkout remain available. This is scoped to that banner, not app-wide advertising. |
| Sale-Ending Reminders — Canada | On | Makes supplied promotion-expiry text larger, bold and prominent on list cards. These are in-list reminders; there are no background alarms or invented expiry dates. |

## Using the new features

- **Price history:** open products in your shopping lists. Where the native list price is visible, a small history line appears beneath it. Long-press that price to view timestamps and CAD prices. A long-press opens a scrollable local history dialog; Clear item asks before deleting that item's history. Recording begins with this version; nothing is backfilled. It does not track every product-detail page, run in the background, or notify outside the app.
- **Shopping mode:** enter the app's shopping checklist. Two switches appear above the list. Shopping mode keeps this screen awake and enlarges checkboxes. Show collected items restores collapsed rows so you can uncheck them. Turning Shopping mode off restores normal row display and screen behaviour. The native list and checkoff state are never deleted or modified by the hiding feature.
- **Unit prices:** list cards show the existing unit-price string in larger bold text. Missing values remain absent. This is clearer display, not automatic normalization between unrelated measurement units.
- **Aisle sorting:** applied whenever the checklist receives an updated list. The patch changes the display order only. It uses structured zone/aisle numbers, falling back to Walmart's displayed location. Items without a location appear last. If every location is missing, the original order remains. This is not a shortest-route map.
- **Running total:** appears above the checklist with total, remaining and collected amounts. It reads all items currently loaded by the app (not just visible cards) and refreshes once per second while the checklist is visible. Server-supplied line prices are used once, without multiplying quantity again. When a line price is missing, an exact unit price times a positive requested quantity is used only for EACH products. Missing prices, price ranges, suppressed offers, and unknown-weight calculations are excluded and counted. Amounts are estimated CAD before tax, deposits, delivery fees or checkout-only discounts. Loaded prices may be stale offline.
- **Scanner:** tap Scan barcode above the checklist. This opens the normal Walmart product scanner; it does not silently add scanned items to the list or provide Scan & Go checkout. Back returns through the native navigation flow.
- **Promotions:** the deals-list promotional banner collapses. Product savings and offer terms remain visible. Sale-ending reminders emphasize expiry text that Walmart already supplies for the item's current promotion; if the app supplies none, no deadline is fabricated.

The shopping controls, total and scanner share one container. Each patch can be selected independently. Shopping mode now also handles the separate collected-item adapter, so its Show collected items switch applies to both list sections.

Price histories are grouped by product ID, offer, seller, sales unit and the store name/address displayed on a checklist. Without an identifiable store they are explicitly labelled **store not identified** and kept apart from identified store histories. Fulfilment options may still differ. Price ranges are excluded. Up to 128 histories and 30 observations each are retained, with at most one unchanged-price observation per local day. Price changes within a day are retained. History is stored in the Walmart app's private data and is lost if that data is cleared or the patched app is uninstalled. No history is uploaded by these patches.

## Upstream features not included

The US Route My List and Flash Everywhere patches depend on native map-screen and shelf-tag implementations absent from the inspected Canadian 26.40 build. Shared data-model classes alone do not provide those screens or services. This bundle therefore does not claim those two features or include empty placeholder patches.

## Validation and limits

All twelve patches are applied together to the original Canada 26.40 base APK with Morphe Desktop 1.20.0 and `--validate`. The APK rebuild and alignment complete successfully. Seventy-five JVM checks cover navigation, stock, price history, natural aisle ordering, stable grouping, immutable input preservation, model-field extraction, promotion selection and subtotal rules (including quantity, rounding, zero prices, missing weights and suppressed offers). The five new patches are also validated as an independent selection. DEX inspection verifies injected calls and the runtime methods/fields they depend on. See `validation/` in the accompanying source archive for exact evidence.

This is a **device-untested beta**. Successful patching and bytecode checks do not establish that the Android UI, login, and shopping flows work on a phone. No on-device or live Walmart account testing has been performed. APK architectures were not all independently tested; the inspected distribution contained armeabi-v7a splits. A version restriction prevents silently applying these obfuscated hooks to other releases. Select 26.40 and do not force compatibility for another version.

## Building

Dependencies used: Eclipse Temurin JDK 21.0.13+9; Morphe Desktop 1.20.0 all-in-one JAR; Gradle 9.8.0 distribution (its Kotlin compiler libraries, compiled with language/API level 2.2); Android API 35 android.jar; Google R8 8.13.17.

Place `morphe.jar`, `r8.jar`, `android.jar` and the extracted JDK directory in a tools directory, then run:

```sh
python3 build.py --tools /path/to/tools --kotlin-libs /path/to/gradle-9.8.0/lib
```

The script compiles the extension, runs logic checks, compiles the patches, produces Android DEX files and packages the `.mpp`. It does not require GitHub Packages credentials. `dependencies.json` records dependency and input hashes for this build.

To apply all twelve patches and validate unique fingerprints:

```sh
java -Xmx3g -jar /path/to/tools/morphe.jar patch original-canada.apk \
  -p walmart-canada-26.40-v1.2.0-beta.1.mpp \
  -e 'Remove Shop — Canada' --validate --unsigned \
  -o patched-validation.apk -r patch-results.json
```

## Source and license

Based on https://github.com/R2bEEaton/walmart-morphe at commit `25ea2dd699ec08c30ef2451b96f14ea9dd1698d2` (v3.1.0). The Find item extension is adapted from that GPLv3 project; its notice and license are included. Other hooks and the standalone build path were written for this Canadian port. Changes include Canadian class/enum mappings, resolving resource IDs by name instead of US fallback values, post-setter injection at every return, explicit unknown stock states, and navigation-map copying. The complete corresponding source for this MPP is included in the accompanying source ZIP.
