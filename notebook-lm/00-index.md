# Diseño mantenible en Android: LSP, ISP y efectos de Compose

## Propósito

Este notebook reúne dos líneas de aprendizaje aplicadas en Android:

1. Diseñar contratos pequeños y sustituibles mediante los principios de
   sustitución de Liskov (LSP) y segregación de interfaces (ISP).
2. Gestionar trabajo asíncrono y recursos externos desde Jetpack Compose mediante
   `LaunchedEffect`, `DisposableEffect` y `rememberUpdatedState`.

El objetivo común es mejorar claridad, mantenibilidad, capacidad de prueba y
evolución de la UI. Los ejemplos de arquitectura se han simplificado y
anonimizado; el laboratorio Compose es un proyecto Android ejecutable.

## Audiencia

- Desarrolladores Android con conocimientos de Kotlin y Compose.
- Nivel intermedio.
- No se presupone experiencia avanzada con SOLID ni con el runtime de Compose.

## Resultados de aprendizaje

Al terminar, la persona debería poder:

- detectar una interfaz que obliga a implementar operaciones innecesarias;
- definir contratos pequeños orientados a consumidores concretos;
- sustituir implementaciones reales y dobles de prueba sin alterar el contrato;
- explicar cuándo se inicia, reinicia y cancela un `LaunchedEffect`;
- registrar y liberar correctamente un recurso con `DisposableEffect`;
- elegir claves estables y completas;
- usar `rememberUpdatedState` cuando un valor debe actualizarse sin reiniciar;
- probar estos comportamientos mediante secuencias observables.

## Fuentes del notebook

| Fuente | Contenido |
|---|---|
| `01-objective-and-evidence.md` | Objetivo, hitos y evidencias disponibles |
| `02-lsp-isp-real-case.md` | Aplicación de LSP e ISP en un caso Android real anonimizado |
| `03-launched-effect.md` | Ciclo de vida, claves y cancelación de `LaunchedEffect` |
| `04-disposable-effect.md` | Registro, sustitución y liberación de recursos |
| `05-keys-updated-state-and-cases.md` | Claves, `rememberUpdatedState` y casos prácticos |
| `06-tests-exercises-and-checklist.md` | Pruebas, ejercicios y checklist de revisión |

## Ruta recomendada

1. Leer el objetivo y separar sus cuatro hitos.
2. Estudiar el caso LSP/ISP y relacionar cada contrato con su consumidor.
3. Abrir Compose Effect Lab y predecir cada secuencia antes de ejecutarla.
4. Cambiar claves, retirar composables y observar la línea temporal.
5. Revisar las pruebas automatizadas.
6. Resolver los ejercicios de transferencia sobre un proyecto autorizado.

## Instrucciones para NotebookLM

- Utilizar solo las fuentes seleccionadas y las referencias oficiales añadidas.
- Diferenciar evidencia real, ejemplo anonimizado y recomendación general.
- No presentar Compose Effect Lab como arquitectura completa de producción.
- No inventar APIs, resultados de tests ni detalles internos de los proyectos.
- Explicar cada decisión mediante el consumidor, el contrato y el ciclo de vida.

