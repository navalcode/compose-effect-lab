# Guía didáctica

## El problema que resuelven los efectos

Una función `@Composable` describe interfaz y puede recomponerse muchas veces.
Registrar listeners, lanzar coroutines o enviar analítica directamente durante la
composición produciría ejecuciones repetidas y difíciles de controlar.

Las APIs de efectos proporcionan fronteras explícitas entre la composición y el
mundo externo. Su comportamiento depende de dos cosas:

- si el composable continúa o no en la composición;
- si cambia alguna de las claves declaradas.

## `LaunchedEffect`

Ejecuta una coroutine ligada a la composición:

```kotlin
LaunchedEffect(itemId) {
    repository.observe(itemId).collect(::render)
}
```

- Al entrar, inicia la coroutine.
- Si cambia `itemId`, cancela la anterior e inicia otra.
- Si el composable sale de composición, cancela la coroutine.
- Es apropiado para trabajo suspendible controlado por estado Compose.

No hace falta para una acción directa de botón. En ese caso, el evento pertenece
normalmente al callback `onClick` o a un propietario como el ViewModel.

## `DisposableEffect`

Adquiere un recurso que necesita liberación explícita:

```kotlin
DisposableEffect(source) {
    val listener = source.register(::onEvent)
    onDispose { listener.unregister() }
}
```

- Al entrar, registra el recurso.
- Si cambia `source`, libera el anterior y registra el nuevo.
- Al salir de composición, ejecuta `onDispose`.
- Si se adquiere un recurso, `onDispose` debe liberarlo.

## `rememberUpdatedState`

Un efecto de larga vida puede necesitar usar el callback o valor más reciente sin
reiniciarse:

```kotlin
val currentOnTimeout by rememberUpdatedState(onTimeout)

LaunchedEffect(Unit) {
    delay(3_000)
    currentOnTimeout()
}
```

Aquí `Unit` expresa que la identidad del efecto no cambia. El callback sí puede
actualizarse durante la espera.

## Regla para elegir claves

Una clave debe representar la identidad que invalida el trabajo o recurso:

- Falta una clave necesaria: el efecto puede conservar datos obsoletos.
- Sobra una clave: el efecto se reinicia innecesariamente.
- La clave es inestable: puede reiniciarse en cada recomposición.

## Método de práctica

Para cada demo:

1. Predecir la secuencia exacta de eventos.
2. Ejecutar sin cambiar claves.
3. Ejecutar cambiando la clave.
4. Retirar el composable de la composición.
5. Comparar la línea temporal con la predicción.
6. Localizar la prueba que demuestra ese contrato.

