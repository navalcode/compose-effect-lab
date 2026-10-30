# Compose Effect Lab

[English](README.md) · [CI](https://github.com/navalcode/compose-effect-lab/actions/workflows/checks.yml)

Aplicación Android para comparar LaunchedEffect, DisposableEffect y rememberUpdatedState. Cambia claves y retira ejemplos de composición; después consulta los eventos de inicio, cancelación y liberación. Los ejemplos se ejecutan localmente, sin servicio de analítica.

<img src="docs/images/launched-es.png" width="300" alt="Compose Effect Lab ejecutado en un emulador Android">

## Qué puedes probar

1. Abre **LaunchedEffect**, inserta el efecto, cambia su clave y retíralo. Compara los eventos `start` y `cancel`.
2. En **Valor actualizado**, inicia la entrega de tres segundos y cambia el mensaje durante la espera. Se entrega el valor más reciente sin reiniciar el efecto. Puedes repetir la entrega.
3. Registra un listener en **DisposableEffect**, cambia su clave de recurso y libéralo. GPS, Lifecycle y Sensor son nombres de recursos simulados; no se leen ubicación ni sensores.
4. En **Casos**, mantén el mismo ID de pantalla en composición durante dos segundos para registrar un evento ficticio. Cambiar el ID cancela la espera anterior. El observador de ciclo de vida usa el LifecycleOwner real y se retira al salir su ejemplo de composición.

Desplazar la lista no retira el experimento de composición. Cambiar su clave, retirarlo o salir de su pestaña sí cancela o libera su trabajo. Borrar eventos solo limpia el registro.

## Compilar y ejecutar

Usa JDK 17, Android SDK 36 y API 26 o superior para instalar. Abre el proyecto en Android Studio o configura el SDK en un `local.properties` sin seguimiento. Desde este directorio:

```sh
./gradlew :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
./gradlew :app:installDebug
```

El wrapper de Gradle 8.13 verifica el SHA-256 de su distribución. Las dependencias están fijadas en `gradle/libs.versions.toml`. Las reglas de idioma de los tests nativos requieren API 33+; sustituye `DEVICE_ID` por un serial concreto:

```sh
ANDROID_SERIAL=DEVICE_ID ./gradlew :app:connectedDebugAndroidTest
```

## Leer el código

- [EffectDemos.kt](app/src/main/java/com/example/composeeffectlab/ui/EffectDemos.kt): coroutine con clave, ciclo de vida del listener y callback actualizado.
- [ComposeEffectLabApp.kt](app/src/main/java/com/example/composeeffectlab/ui/ComposeEffectLabApp.kt): controles, efectos fuera de celdas lazy y los dos casos prácticos.
- [FakeListenerRegistry.kt](app/src/main/java/com/example/composeeffectlab/domain/FakeListenerRegistry.kt): registros simulados.
- [EffectEventRecorder.kt](app/src/main/java/com/example/composeeffectlab/model/EffectEventRecorder.kt): snapshots ordenados de eventos.
- [EffectScreenTest.kt](app/src/androidTest/java/com/example/composeeffectlab/ui/EffectScreenTest.kt): scroll, sustitución, entrega repetida y liberación del listener.

Los tests JVM comprueban el registro de eventos y los recursos simulados. Los tests instrumentados verifican los efectos de Compose; la prueba de entrada real abre MainActivity por separado. Los mensajes técnicos de las pruebas mantienen sus identificadores para poder compararlos con el código.

Guías detalladas en español:

- [Guia didactica](docs/01_GUIA_DIDACTICA.md)
- [Comparativa](docs/02_COMPARATIVA.md)
- [Ejercicios](docs/03_EJERCICIOS.md)
- [Material para compartir](docs/04_MATERIAL_PARA_COMPARTIR.md)

## Comprobaciones y límites

La suite contiene **4 tests JVM y 9 nativos**. [Verificación](docs/VERIFICATION.md), en inglés, recoge las ejecuciones, capturas, avisos de auditoría y la diferencia entre cobertura nativa local y CI. [DESIGN.md](DESIGN.md) explica la interfaz.

Hay recursos españoles con inglés de respaldo. No se atribuye validación física, certificación de accesibilidad, release optimizada ni publicación en tiendas. Se conservan licencias y atribuciones de fuentes/dependencias en [avisos de terceros](THIRD_PARTY.md).
