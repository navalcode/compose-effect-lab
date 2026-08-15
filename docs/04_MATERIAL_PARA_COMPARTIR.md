# Material para una sesión de 20 minutos

## Título sugerido

**Efectos predecibles en Compose: claves, cancelación y limpieza**

## Agenda

1. **2 min — El problema:** una composición puede ejecutarse muchas veces.
2. **5 min — LaunchedEffect:** entrada, cambio de clave y cancelación.
3. **5 min — DisposableEffect:** registro, `onDispose` y ausencia de fugas.
4. **3 min — rememberUpdatedState:** actualizar sin reiniciar.
5. **4 min — Casos:** analítica estable y LifecycleObserver.
6. **1 min — Cierre:** árbol de decisión y ejercicios.

## Tres ideas que deben quedar

- La composición describe UI; los efectos controlan interacciones externas.
- Las claves representan la identidad que invalida un efecto.
- Todo recurso adquirido por `DisposableEffect` debe liberarse en `onDispose`.

## Preguntas para la comunidad

- ¿Qué `LaunchedEffect(Unit)` de nuestro código depende realmente de otra clave?
- ¿Qué listeners se registran desde Compose y quién garantiza su liberación?
- ¿Qué trabajo debería pertenecer al ViewModel en lugar de a la pantalla?
- ¿Qué callbacks deben actualizarse sin reiniciar una operación larga?

## Evidencia del objetivo

- Proyecto ejecutable y sin información propietaria.
- Demos interactivas de ambos efectos.
- Caso explícito de `rememberUpdatedState`.
- Dos ejemplos transferibles.
- Tests unitarios e instrumentados.
- Compilación del APK.
- Publicación, enlace o acta conservada como evidencia de comunidad.

