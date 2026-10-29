# Compose Effect Lab

[Español](README.es.md)

An interactive Android lab for understanding `LaunchedEffect`, `DisposableEffect` and `rememberUpdatedState`. Fictional, self-contained examples record a timeline so effect ownership and cleanup can be observed.

The learning loop is **predict, run, observe and explain**: insert an effect, change its identity, update callbacks, remove it from composition and compare the resulting events.

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

Four JVM tests, lint and the debug APK build passed locally on 29 October 2026 with JDK 17. Lint reported no errors and two warnings; the existing build configuration suppresses dependency-version notices. Four instrumented lifecycle tests also passed on an API 37 emulator. This device result is separate from the JVM tests.

The debug artifact has aligned native libraries but no R8 metadata. This is not an optimized-release, physical-device or store-distribution claim.
