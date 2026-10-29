[English](README.md) · **Español**

# Compose Effect Lab

Proyecto Android didáctico para comprender y aplicar correctamente
`LaunchedEffect`, `DisposableEffect` y `rememberUpdatedState` en Jetpack Compose.
Los ejemplos son ficticios, autocontenidos y no incluyen código ni datos
propietarios.

La dinámica es **predecir, ejecutar, observar y explicar**. Cada laboratorio
muestra una línea temporal para hacer visible cuándo comienza un efecto, cuándo
se reinicia, cuándo se cancela y cuándo libera un recurso.

## Qué cubre

| Hito | Evidencia |
|---|---|
| Comprender `LaunchedEffect` | Demo con clave, salida de composición y cancelación |
| Comprender `DisposableEffect` | Registro y liberación de listeners simulados |
| Elegir claves correctas | Cambio interactivo de identidad y línea temporal |
| Evitar reinicios innecesarios | Demo con `rememberUpdatedState` |
| Aplicar efectos a casos prácticos | Analítica temporizada y `LifecycleObserver` |
| Demostrar el comportamiento | Tests unitarios e instrumentados de Compose |

## Ejecutar

1. Abrir `ComposeEffectLab` con Android Studio.
2. Esperar a que finalice la sincronización de Gradle.
3. Seleccionar un emulador o dispositivo con Android 8 / API 26 o superior.
4. Ejecutar la configuración `app`.

Validación por terminal:

```bash
./gradlew testDebugUnitTest assembleDebug
./gradlew connectedDebugAndroidTest
```

El segundo comando requiere un dispositivo o emulador conectado.

## Ruta recomendada

1. Leer `docs/01_GUIA_DIDACTICA.md`.
2. Insertar `LaunchedEffect`, cambiar su clave y retirarlo de composición.
3. Probar `rememberUpdatedState` cambiando el mensaje durante la espera.
4. Registrar un listener con `DisposableEffect`, cambiar su clave y liberarlo.
5. Revisar la tabla de `docs/02_COMPARATIVA.md`.
6. Ejecutar los dos casos reales y resolver `docs/03_EJERCICIOS.md`.

## Arquitectura

```text
Compose UI ──claves/presencia──> Effects
    │                               │
    ├── línea temporal <────────────┤
    │                               ├── coroutine suspendible
    │                               └── recurso registrable
    └── clases puras testeables: recorder + fake registry
```

## Referencia oficial

- [Side-effects in Compose](https://developer.android.com/develop/ui/compose/side-effects)


## Verificación del escaparate

Comprobado el 29 de octubre de 2026: cuatro tests JVM, cuatro instrumentados en emulador API 37, lint sin errores (dos avisos) y APK debug. No equivale a validación física, release optimizada o aprobación de tienda.

El wrapper comprueba SHA-256 de Gradle 8.13. La CI se comprueba por separado; las evidencias anteriores son locales. Se conservan las licencias existentes.
