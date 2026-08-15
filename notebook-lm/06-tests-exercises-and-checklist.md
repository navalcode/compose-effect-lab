# Pruebas, ejercicios y checklist

## Estrategia de pruebas

Compose Effect Lab separa dos niveles:

- Tests JVM para el registro ordenado de eventos y la liberación idempotente.
- Tests instrumentados de Compose para el ciclo de vida real de los efectos.

Las pruebas de UI no inspeccionan detalles internos; verifican secuencias
observables.

## Cuatro contratos instrumentados

### Reinicio por clave

```text
start:1 → cancel:1 → start:2
```

### Salida de composición

```text
start:7 → cancel:7
```

### Valor actualizado sin reinicio

```text
start → delivered:updated
```

### Liberación y nuevo registro

```text
register:GPS → dispose:GPS → register:Lifecycle → dispose:Lifecycle
```

## Validación reproducible

```bash
./gradlew testDebugUnitTest lintDebug assembleDebug
./gradlew connectedDebugAndroidTest
```

El segundo comando requiere un emulador o dispositivo Android conectado.

## Ejercicios de observación

1. ¿Una recomposición sin cambio de clave reinicia `LaunchedEffect`?
2. ¿Qué ocurre primero al cambiar una clave de `DisposableEffect`?
3. ¿Por qué retirar el composable cancela la coroutine aunque la Activity siga viva?
4. ¿Qué recurso queda activo después de ocultar la demo de listener?
5. ¿Por qué el valor actualizado se entrega sin un segundo evento `start`?

## Ejercicios de modificación

1. Añadir el valor como clave de la demo con `rememberUpdatedState` y comparar la
   nueva secuencia.
2. Introducir una clave inestable y observar los reinicios.
3. Añadir un segundo listener y diseñar su liberación.
4. Cambiar la identidad durante el delay de analítica.
5. Probar que una implementación alternativa de `AlertSpeaker` respeta el mismo
   contrato que la implementación Android.

## Checklist LSP/ISP

- Cada interfaz responde a una responsabilidad concreta.
- Ningún consumidor depende de métodos que no utiliza.
- Las implementaciones respetan precondiciones y resultados del contrato.
- Los fakes no permiten comportamientos incompatibles con producción.
- Un nuevo subtipo no necesita operaciones vacías o no soportadas.
- Los tests se escriben contra comportamiento observable.

## Checklist de efectos Compose

- El efecto es necesario; no es una acción normal de `onClick`.
- El propietario correcto es la composición y no un ViewModel.
- Todas las identidades que invalidan el efecto están en las claves.
- Valores que no deben reiniciar usan `rememberUpdatedState` cuando procede.
- Todo recurso de `DisposableEffect` se libera en `onDispose`.
- La cancelación no se transforma en un error de negocio.
- Existe una prueba o secuencia observable para el cambio de clave y la salida.

## Evidencia para cerrar el objetivo

- Comentario de LSP/ISP y refactorización real.
- Repositorio o ZIP de Compose Effect Lab.
- Resultado de tests, lint y compilación.
- Notebook compartido.
- Publicación en Teams con enlace al notebook y al proyecto.
- Captura o enlace conservado en la actividad del objetivo.

