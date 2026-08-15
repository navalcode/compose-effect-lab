# `DisposableEffect`: adquirir y liberar recursos

## Modelo mental

`DisposableEffect` se utiliza cuando un composable registra un recurso externo
que necesita una liberación explícita.

- Al entrar, adquiere o registra el recurso.
- Si cambia una clave, ejecuta `onDispose` para el recurso anterior y crea el nuevo.
- Al salir de composición, ejecuta `onDispose`.
- La liberación debe ser segura y corresponder exactamente al recurso adquirido.

## Ejemplo ejecutable

```kotlin
@Composable
fun DisposableEffectProbe(
    effectKey: String,
    registry: FakeListenerRegistry,
) {
    DisposableEffect(effectKey) {
        val registration = registry.register(effectKey)
        onDispose(registration::dispose)
    }
}
```

Si la clave cambia de `GPS` a `Lifecycle` y después el composable desaparece, la
secuencia esperada es:

```text
register:GPS
dispose:GPS
register:Lifecycle
dispose:Lifecycle
```

El orden evita que dos registros incompatibles permanezcan activos y demuestra
que no queda un recurso abandonado al retirar la UI.

## Casos apropiados

- `LifecycleEventObserver`.
- Listeners de sensores o localización.
- Callbacks de un SDK externo.
- Broadcast receivers registrados dinámicamente.
- Suscripciones que no utilizan una API suspendible.

## Caso real del laboratorio

```kotlin
val owner = LocalLifecycleOwner.current

DisposableEffect(owner) {
    val observer = LifecycleEventObserver { _, event ->
        record(event.name)
    }
    owner.lifecycle.addObserver(observer)

    onDispose {
        owner.lifecycle.removeObserver(observer)
    }
}
```

El `LifecycleOwner` es la clave porque identifica el objeto en el que se registra
el observer. Si cambia, el registro anterior deja de ser válido.

## Errores frecuentes

- `onDispose` vacío o que no libera el recurso adquirido.
- Registrar directamente en el cuerpo del composable.
- Usar una clave que no representa la fuente del registro.
- Liberar un listener diferente al registrado.
- Mantener el recurso activo fuera de la vida útil de la pantalla.
- Usar `DisposableEffect` para trabajo suspendible que encaja mejor en
  `LaunchedEffect`.

## Referencia oficial

- [Side-effects in Compose](https://developer.android.com/develop/ui/compose/side-effects)

