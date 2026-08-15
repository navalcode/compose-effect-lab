# Ejercicios

## Nivel 1 — Observar

1. Insertar el `LaunchedEffect` con clave 1. ¿Qué evento aparece?
2. Cambiar a clave 2. Predecir el orden entre cancelación e inicio.
3. Retirar el efecto. ¿Por qué se cancela aunque la Activity siga viva?
4. Registrar GPS y cambiar a Lifecycle. ¿Qué recurso se libera primero?
5. Iniciar la entrega diferida y cambiar el mensaje. ¿Por qué no aparece un
   segundo evento `start`?

## Nivel 2 — Modificar

1. Añadir el mensaje como clave del ejemplo de `rememberUpdatedState`. Comprobar
   que ahora el delay se reinicia y explicar por qué cambia el contrato.
2. Eliminar temporalmente `onDispose` del registro simulado. Anotar el error de
   compilación o diseñar una liberación correcta; restaurarlo después.
3. Usar una clave creada como objeto nuevo en cada recomposición. Observar los
   reinicios y sustituirla por una identidad estable.
4. Reducir la espera de analítica y navegar antes y después del límite.
5. Añadir un segundo observer con una clave independiente y predecir su limpieza.

## Nivel 3 — Transferir

1. Buscar en un proyecto autorizado un `LaunchedEffect(Unit)`.
2. Documentar de qué valores depende realmente su trabajo.
3. Decidir cuáles son claves y cuáles requieren `rememberUpdatedState`.
4. Buscar un listener registrado desde Compose y comprobar que siempre se libera.
5. Escribir una prueba que cambie la clave y verifique el orden observable.

## Preguntas de revisión

1. ¿Una recomposición reinicia siempre `LaunchedEffect`?
2. ¿Qué ocurre antes al cambiar una clave de `DisposableEffect`: registrar lo
   nuevo o liberar lo anterior?
3. ¿Por qué un callback puede estar obsoleto dentro de una coroutine larga?
4. ¿Cuándo debería el trabajo trasladarse al ViewModel?
5. ¿Qué problema produce una clave inestable?

## Criterio de finalización

- Explicar la diferencia entre recomposición y salida de composición.
- Predecir inicio, cancelación y liberación al cambiar una clave.
- Implementar un caso correcto de cada efecto.
- Justificar el uso de `rememberUpdatedState`.
- Demostrar el comportamiento mediante una prueba.

