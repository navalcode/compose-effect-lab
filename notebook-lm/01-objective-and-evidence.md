# Objetivo y evidencias

## Objetivo de desarrollo

Reforzar los principios de diseño LSP e ISP y el uso adecuado de efectos en
Jetpack Compose para mejorar claridad, mantenibilidad y escalabilidad de la UI.

La aplicación práctica se divide en cuatro hitos:

1. Revisar LSP e ISP en ejemplos reales de un proyecto Android.
2. Refactorizar al menos una interfaz o jerarquía real.
3. Implementar correctamente un caso con `LaunchedEffect` y otro con
   `DisposableEffect`.
4. Compartir los conocimientos con la comunidad Android.

## Evidencia de LSP e ISP

En un proyecto Android Automotive real se separó la coordinación de alertas en
contratos específicos:

- un contrato para despachar alertas;
- un contrato para sintetizar voz;
- un contrato para mostrar notificaciones;
- un proveedor específico de textos de peaje.

Las implementaciones Android y los dobles de prueba pueden sustituirse a través
de esas interfaces sin cambiar el comportamiento esperado por los consumidores.
Esto reduce dependencias innecesarias y permite probar coordinación, deduplicación,
foreground/background y resultados de tránsito de manera aislada.

También se revisaron jerarquías de pantallas y servicios para centralizar
comportamiento común sin obligar a subtipos a depender de operaciones que no
necesitan.

## Evidencia de efectos Compose

El proyecto **Compose Effect Lab** contiene:

- una demo de `LaunchedEffect(key)` con inicio, cambio de clave y cancelación al
  salir de composición;
- una demo de `DisposableEffect(key)` con registro y `onDispose` ordenados;
- un caso de `rememberUpdatedState` que utiliza el valor reciente sin reiniciar;
- una analítica temporizada que se cancela al cambiar la identidad de pantalla;
- un `LifecycleEventObserver` registrado y liberado con el ciclo de composición;
- una línea temporal visible para explicar cada transición.

## Evidencia automatizada

La validación realizada el 15 de agosto de 2026 incluye:

- 4 tests unitarios sobre eventos y liberación idempotente de recursos;
- 4 tests instrumentados de Compose ejecutados en `Pixel_9_Pro_XL`;
- `lintDebug` correcto;
- `assembleDebug` correcto.

Las pruebas instrumentadas demuestran:

1. reinicio de `LaunchedEffect` al cambiar la clave;
2. cancelación al salir de composición;
3. uso del valor actualizado sin reiniciar el efecto;
4. liberación del recurso anterior antes de registrar el nuevo.

## Límites de la evidencia

- El laboratorio es didáctico y no incluye red, persistencia o SDK externo.
- Los ejemplos de LSP/ISP se describen de forma anonimizada y sin reglas de
  negocio propietarias.
- La aportación a comunidad solo se considera finalizada después de publicar el
  notebook y conservar el enlace o captura de la publicación.
