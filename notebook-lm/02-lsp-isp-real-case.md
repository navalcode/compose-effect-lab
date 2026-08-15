# LSP e ISP aplicados a un caso Android real

## ISP: contratos orientados al consumidor

El principio de segregación de interfaces propone que un consumidor no dependa
de operaciones que no necesita. En un sistema de alertas, una interfaz general
que mezcle voz, notificaciones, textos, estado de foreground y deduplicación
obligaría a cada implementación y cada test a conocer demasiadas responsabilidades.

La refactorización divide el problema en contratos pequeños:

```kotlin
interface AlertDispatcher {
    fun emit(alert: Alert, language: String? = null)
}

interface AlertSpeaker {
    fun speak(text: String, language: String?, priority: Priority): Boolean
}

interface AlertNotifications {
    fun show(alert: Alert)
}

interface TollAlertTextProvider {
    fun title(): String
    fun successMessage(): String
    fun notAuthorizedMessage(): String
}
```

Cada consumidor recibe únicamente el contrato que utiliza. El servicio de peajes
no necesita conocer cómo se reproduce voz ni cómo se construye una notificación;
solo despacha una alerta y obtiene sus textos.

## LSP: sustitución sin sorpresas

El principio de sustitución de Liskov exige que una implementación pueda ocupar
el lugar del contrato sin romper las expectativas del consumidor.

En este caso:

- una implementación Android del speaker y un fake de test aceptan los mismos
  datos y respetan la misma semántica de resultado;
- una implementación de notificaciones y un fake pueden sustituirse sin que el
  coordinador cambie;
- el proveedor Android de textos y el proveedor de test ofrecen el mismo conjunto
  de mensajes;
- el dispatcher preserva reglas observables como deduplicación y prioridad.

Un fake no es sustituible si evita precondiciones, cambia el significado del
resultado o permite secuencias que la implementación real rechaza. Por eso los
tests deben comprobar comportamiento, no solo llamadas.

## Coordinación y separación

```text
Servicio de negocio
       │
       ▼
AlertDispatcher
       │
       ▼
AlertCoordinator ──> AlertSpeaker
       │
       └────────────> AlertNotifications
```

El coordinador decide deduplicación y comportamiento foreground/background. Los
adaptadores Android ejecutan voz y notificación. Esta frontera permite cambiar un
adaptador sin alterar el servicio de negocio.

## Mejora obtenida

- Tests con dobles pequeños y explícitos.
- Menor acoplamiento entre peajes, voz y notificaciones.
- Sustitución de implementaciones sin cambios en consumidores.
- Evolución independiente de contratos.
- Responsabilidades más fáciles de localizar y explicar.

## Preguntas de revisión

- ¿Qué métodos utiliza realmente cada consumidor?
- ¿Puede un fake sustituir a la implementación real respetando el mismo contrato?
- ¿Algún subtipo necesita lanzar `UnsupportedOperationException`?
- ¿Una nueva funcionalidad obliga a modificar consumidores no relacionados?
- ¿Las precondiciones y resultados mantienen el mismo significado?

