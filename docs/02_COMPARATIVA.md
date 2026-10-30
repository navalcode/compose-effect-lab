# Comparativa y reglas de decisión

## Tabla principal

| Necesidad | API | Finalización |
|---|---|---|
| Ejecutar trabajo suspendible ligado a estado Compose | `LaunchedEffect(keys)` | Cancela la coroutine al cambiar clave o salir |
| Registrar y liberar un recurso externo | `DisposableEffect(keys)` | Ejecuta `onDispose` al cambiar clave o salir |
| Usar el valor más reciente sin reiniciar un efecto | `rememberUpdatedState(value)` | No crea un efecto por sí mismo |
| Ejecutar una acción directa del usuario | Callback como `onClick` | Depende del propietario de la acción |
| Trabajo que debe sobrevivir a la pantalla | ViewModel o capa apropiada | No debe pertenecer al composable |

## Árbol de decisión

```text
¿Es una acción directa del usuario?
├── Sí → onClick / evento hacia ViewModel
└── No
    ├── ¿Es trabajo suspendible ligado a la presencia o identidad de la UI?
    │   └── Sí → LaunchedEffect(claves)
    └── ¿Hay que registrar y liberar un recurso?
        └── Sí → DisposableEffect(claves) + onDispose
```

Si un efecto necesita un callback reciente pero ese callback no cambia su
identidad, se combina con `rememberUpdatedState`.

## Casos del laboratorio

### Cambio de identidad

`LaunchedEffect(screenId)` espera dos segundos antes de registrar un evento ficticio.
Cambiar el ID o retirar el ejemplo de composición cancela la espera anterior.
Mantener el mismo ID en composición permite terminarla. No se envían datos a un servicio.

### Observador del ciclo de vida

`DisposableEffect(lifecycleOwner)` registra un `LifecycleEventObserver`. Si cambia
el owner o el ejemplo sale de composición, `onDispose` retira exactamente ese
observer.

## Anti-patrones

- Llamar a una API externa directamente en el cuerpo del composable.
- Usar `LaunchedEffect(Unit)` aunque el trabajo dependa de una identidad cambiante.
- Incluir un callback cambiante como clave cuando no se desea reiniciar.
- Registrar un recurso que necesita liberación y dejar `onDispose` vacío.
- Lanzar desde un composable trabajo que debe sobrevivir a la pantalla.
- Confundir recomposición con entrada o salida de composición.

