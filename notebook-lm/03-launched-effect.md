# `LaunchedEffect`: trabajo suspendible ligado a Compose

## Modelo mental

`LaunchedEffect` ejecuta una coroutine cuya vida está ligada a una posición de la
composición y a un conjunto de claves.

- Al entrar en composición, inicia la coroutine.
- Si cambia una clave, cancela la coroutine anterior e inicia otra.
- Al salir de composición, cancela el trabajo.
- Una recomposición sin cambio de claves no reinicia el efecto.

## Ejemplo ejecutable

```kotlin
@Composable
fun KeyedLaunchedEffectProbe(
    effectKey: Int,
    onEvent: (String) -> Unit,
) {
    LaunchedEffect(effectKey) {
        onEvent("start:$effectKey")
        try {
            awaitCancellation()
        } finally {
            onEvent("cancel:$effectKey")
        }
    }
}
```

Con clave 1 y después clave 2, la secuencia esperada es:

```text
start:1
cancel:1
start:2
```

Si el composable desaparece después de `start:7`, la secuencia termina con
`cancel:7`.

## Qué debe ser una clave

Una clave representa la identidad que invalida el trabajo. Si una petición carga
datos de un usuario, el identificador del usuario suele formar parte de la clave.

- Clave insuficiente: la coroutine conserva datos obsoletos.
- Clave excesiva: trabajo válido se cancela y reinicia.
- Clave inestable: reinicios frecuentes sin cambio real de identidad.

## Cuándo usarlo

- Coleccionar un Flow mientras un composable está presente.
- Ejecutar una operación suspendible gobernada por una identidad de UI.
- Aplicar un delay, timeout o animación ligado a composición.
- Sincronizar estado Compose con una API suspendible.

## Cuándo no usarlo

- Para ejecutar lógica directamente durante la composición.
- Para una acción normal de botón que pertenece a `onClick`.
- Para trabajo que debe sobrevivir a la pantalla; ese trabajo necesita un
  propietario más estable, como un ViewModel o una capa de datos.
- Para ocultar una política de reintentos o ciclo de vida no definida.

## Caso del laboratorio: analítica estable

```kotlin
LaunchedEffect(screenId) {
    delay(2_000)
    analytics.sendScreen(screenId)
}
```

Si cambia `screenId` antes de dos segundos, el envío anterior se cancela. El
operador expresa que solo debe registrarse la pantalla que ha permanecido estable.

## Referencia oficial

- [Side-effects in Compose](https://developer.android.com/develop/ui/compose/side-effects)

