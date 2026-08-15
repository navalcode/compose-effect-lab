package com.example.composeeffectlab.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import com.example.composeeffectlab.domain.FakeListenerRegistry
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay

@Composable
fun KeyedLaunchedEffectProbe(
    effectKey: Int,
    onEvent: (String) -> Unit,
) {
    LaunchedEffect(effectKey) {
        onEvent("start:$effectKey")
        try {
            awaitCancellation()
        } finally {
            onEvent("cancel:$effectKey")
        }
    }
}

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

