package com.example.composeeffectlab.model

enum class EventKind {
    START,
    TICK,
    INFO,
    DISPOSE,
}

data class EffectEvent(
    val id: Long,
    val source: String,
    val message: String,
    val kind: EventKind = EventKind.INFO,
)

