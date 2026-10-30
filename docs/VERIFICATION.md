# Verification record

30 October 2026. Editorial revision of bilingual resources, README, design notes and teaching text. Domain/effect ownership, dependencies and workflow code did not change. The app-bar title now uses one line with ellipsis at enlarged text sizes; its complete text remains in semantics. Core scenario messages and probe identifiers remain stable. Resource-backed instructions and observer labels are revised.

## Current local results

- JDK 17.0.14, SDK 36; all Gradle tasks rerun on the final application sources.
- **4 JVM tests**, zero failures/errors/skips.
- Lint: **0 errors, 1 warning**. Existing target/dependency warnings remain; no warning suppression was added.
- Debug and Android-test APK builds passed. This revision does not build or validate an optimized release.

Dedicated API 37 emulator, 360 × 800 dp:

| Run | Result |
|---|---|
| English, 100% text | 9/9 native tests passed |
| 150% text, qaLanguage=es | 9/9 native tests passed |
| Spanish MainActivity capture, 100% text | 1/1 selected journey passed |

The locale rule belongs to `EffectActivityJourneyTest` only. In the enlarged-text suite, the MainActivity journey uses Spanish; the eight probe/screen tests use the default English locale. They must not be reported as nine Spanish tests. The final APK includes the app-bar title correction; earlier captures with a clipped title were rejected.


The app declares a lower minimum SDK, while native locale rules use API 33+ and the local device is API 37. A minimum-SDK declaration does not prove testing on that version. No physical-device, TalkBack or formal accessibility certification is claimed.

## Commands

From the repository root:

```sh
./gradlew :app:testDebugUnitTest :app:lintDebug :app:assembleDebug :app:assembleDebugAndroidTest
ANDROID_SERIAL=DEVICE_ID ./gradlew :app:connectedDebugAndroidTest
```

Replace `DEVICE_ID` with the serial of a dedicated test emulator. For opt-in language/captures, install the app/test APK and use the Android runner:

```sh
ANDROID_SERIAL=DEVICE_ID ./gradlew :app:installDebug :app:installDebugAndroidTest
adb -s DEVICE_ID shell am instrument -w -r -e qaLanguage es -e captureScreenshots true com.example.composeeffectlab.test/androidx.test.runner.AndroidJUnitRunner
```

The capture argument is optional. It writes test PNGs inside this demo’s private files directory, retrievable with `adb exec-out run-as`. Locale rules restore the previous per-app locale after a test. Use a disposable emulator for viewport/text-scale changes and restore its settings afterward. Screenshots are executed frames, without pixel editing.

The nine native tests comprise four probe contracts, four whole-screen journeys and one MainActivity journey. They distinguish screen composition from the real entry point. Scrolling must not withdraw an active experiment; key/removal changes still cancel/release it. The timed case depends on composition presence, not foreground visibility or lifecycle STARTED. Its event is local; the separate lifecycle observer uses the real owner.

## Artifact audit

Debug APK SHA-256: `c106ae18a3bbd605e3977bdd0420fc91e2b19161f5520571de745c0a21e8cfbd`.

The advisory audit measured **25.59 MB uncompressed DEX**. Native ELF and APK ZIP 16 KB alignment checks passed. R8 percentage metadata is absent. An overall tool pass does not establish an optimization rate, official Play policy, signing or store approval. The audit did not remove dependencies or alter any artifact.

## Hosted CI and earlier evidence

The linked workflow runs JVM tests, lint and the debug build. Native journeys here are local; a green badge does not imply hosted device coverage. Check the workflow against the published commit. Earlier 29 October runs, including dark/landscape checks and a repaired scroll-ownership regression, remain historical evidence and are not silently attributed to this revision.
