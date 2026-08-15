# Claves, `rememberUpdatedState` y casos prácticos

## Valor actualizado sin reiniciar

Un efecto puede tener una vida útil estable y, a la vez, necesitar utilizar el
callback o valor más reciente. Si ese valor se convierte en clave, la coroutine
se cancelará y reiniciará. `rememberUpdatedState` permite actualizar la referencia
capturada sin cambiar la identidad del efecto.

```kotlin
@Composable
fun UpdatedValueEffectProbe(
    value: String,
    delayMillis: Long,
    onEvent: (String) -> Unit,
) {
    val currentValue by rememberUpdatedState(value)

    LaunchedEffect(Unit) {
        onEvent("start")
        delay(delayMillis)
        onEvent("delivered:$currentValue")
    }
}
```

Si `value` cambia de `original` a `updated` durante la espera, el resultado es:

```text
start
delivered:updated
```

No aparece un segundo `start`.

## Regla de decisión

```text
¿Cambiar este dato invalida el trabajo actual?
├── Sí → incluirlo como clave
└── No
    └── ¿El efecto debe usar su valor más reciente?
        ├── Sí → rememberUpdatedState
        └── No → capturarlo de forma estable
```

## Caso 1: callback de timeout

Un timeout debe continuar aunque cambie el callback de navegación. Reiniciar el
delay retrasaría el timeout. El callback se envuelve con `rememberUpdatedState` y
el efecto mantiene una clave estable.

## Caso 2: analítica por identidad

El identificador de pantalla sí invalida el trabajo: navegar a otra pantalla hace
que el envío pendiente de la anterior pierda valor. `screenId` debe ser clave de
`LaunchedEffect`.

## Caso 3: `LifecycleObserver`

El owner determina dónde está registrado el observer y debe ser clave de
`DisposableEffect`. El consumidor de eventos puede actualizarse con
`rememberUpdatedState` si no debe provocar un nuevo registro.

## Comparativa

| Necesidad | Herramienta |
|---|---|
| Trabajo suspendible ligado a composición | `LaunchedEffect(keys)` |
| Recurso registrable con cleanup | `DisposableEffect(keys)` |
| Valor reciente sin reinicio | `rememberUpdatedState(value)` |
| Acción directa del usuario | Callback como `onClick` |
| Trabajo que sobrevive a la pantalla | ViewModel o capa apropiada |

## Preguntas antes de escribir un efecto

- ¿Quién debe poseer realmente este trabajo?
- ¿Qué cambio lo vuelve obsoleto?
- ¿Qué valores solo deben actualizarse?
- ¿Qué recurso se adquiere y cómo se libera?
- ¿Qué secuencia observable demostraría que el comportamiento es correcto?

