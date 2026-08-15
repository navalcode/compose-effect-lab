package com.example.composeeffectlab.model

class EffectEventRecorder(
    private val onChanged: (List<EffectEvent>) -> Unit = {},
) {
    private val mutableEvents = mutableListOf<EffectEvent>()
    private var nextId = 1L

    val events: List<EffectEvent>
        get() = mutableEvents.toList()

    fun record(
        source: String,
        message: String,
        kind: EventKind = EventKind.INFO,
    ): EffectEvent {
        val event = EffectEvent(nextId++, source, message, kind)
        mutableEvents += event
        onChanged(events)
        return event
    }

    fun clear() {
        mutableEvents.clear()
        nextId = 1L
        onChanged(emptyList())
    }
}

