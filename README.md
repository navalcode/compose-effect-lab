# Compose Effect Lab

[Español](README.es.md) · [![CI](https://github.com/navalcode/compose-effect-lab/actions/workflows/checks.yml/badge.svg)](https://github.com/navalcode/compose-effect-lab/actions/workflows/checks.yml)

An interactive Android lab for understanding `LaunchedEffect`, `DisposableEffect` and `rememberUpdatedState`. Fictional, self-contained examples record a timeline so effect ownership and cleanup can be observed.

The learning loop is **predict, run, observe and explain**: insert an effect, change its identity, update callbacks, remove it from composition and compare the resulting events.

## Screen and behavior

<img src="docs/images/launched-en.png" width="300" alt="Real Android emulator: keyed effect controls and the recorded timeline in Compose Effect Lab.">

The screen uses local Outfit typography, a paper/ink/copper palette and open trace rows. Product text supports Spanish with an English fallback. Each experiment owns its effects outside lazy list cells: scrolling a result does not cancel or restart work. The latest-value experiment has its own tab and can deliver repeatedly. [Design and accessibility decisions](DESIGN.md) · [Font notice](THIRD_PARTY.md).

## Compare the effects

| Mechanism | Question to answer |
|---|---|
| `LaunchedEffect` | When should a coroutine start, restart or cancel as keys/presence change? |
| `DisposableEffect` | Which resource belongs to a composition identity, and when is it released? |
| `rememberUpdatedState` | How can running work use the current callback without restarting? |
| Practical examples | How do those choices apply to a fictional timed event and a lifecycle observer? |

The Compose UI controls keys and presence. Pure `EffectEventRecorder` and `FakeListenerRegistry` classes support local tests; instrumented Compose tests inspect actual effect lifecycle behavior. These examples do not collect production analytics.

## Run

Requires JDK 17, Android SDK 36 and API 26+ for installation. Open this directory in Android Studio or configure the SDK in untracked `local.properties`.

```sh
./gradlew :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
./gradlew :app:connectedDebugAndroidTest
```

The second command requires an emulator/device. The Gradle 8.13 wrapper verifies its distribution SHA-256; dependencies are pinned in `gradle/libs.versions.toml`.

## Guided reading

Detailed exercises remain in Spanish:

- [Learning guide](docs/01_GUIA_DIDACTICA.md)
- [Effect comparison](docs/02_COMPARATIVA.md)
- [Exercises](docs/03_EJERCICIOS.md)

Primary reference: [Side-effects in Compose](https://developer.android.com/develop/ui/compose/side-effects).

## Validation scope

Four JVM tests, lint and the debug APK build pass locally with JDK 17. Lint reports zero errors and one target-version warning. Nine instrumented tests pass on an API 37 emulator at 360 × 800 dp and150% text: four probe contracts, four whole-screen journeys and one real MainActivity journey. Additional English/Spanish MainActivity journeys and captures use100% text. The real-entry test uses per-app locale APIs on33+; no lower-API device result is claimed.

The scrolling regression first observed two starts of the same key with large text in the original lazy-cell design. The corrected screen keeps one start until the key/presence actually changes. Other journeys verify key replacement/removal, latest-value delivery without restarting, repeated delivery and listener cleanup when leaving its screen.

The existing CI runs JVM tests, lint and the debug build. Native journeys above were run locally; a successful CI badge does not imply they ran remotely. The debug artifact has aligned native libraries and ZIP entries but no R8 metadata. This is not an optimized-release, physical-device or store-distribution claim.
