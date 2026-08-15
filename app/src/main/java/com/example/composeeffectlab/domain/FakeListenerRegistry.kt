package com.example.composeeffectlab.domain

fun interface DisposableRegistration {
    fun dispose()
}

class FakeListenerRegistry(
    private val onEvent: (String) -> Unit,
) {
    private val mutableActiveKeys = linkedSetOf<String>()

    val activeKeys: Set<String>
        get() = mutableActiveKeys.toSet()

    fun register(key: String): DisposableRegistration {
        mutableActiveKeys += key
        onEvent("register:$key")
        var disposed = false

        return DisposableRegistration {
            if (!disposed) {
                disposed = true
                mutableActiveKeys -= key
                onEvent("dispose:$key")
            }
        }
    }
}

