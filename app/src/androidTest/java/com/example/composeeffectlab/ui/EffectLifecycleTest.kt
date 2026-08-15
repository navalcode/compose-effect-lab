package com.example.composeeffectlab.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.composeeffectlab.domain.FakeListenerRegistry
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EffectLifecycleTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun launchedEffect_restarts_when_key_changes() {
        val events = mutableListOf<String>()
        var effectKey by mutableIntStateOf(1)

        composeRule.setContent {
            KeyedLaunchedEffectProbe(effectKey, events::add)
        }
        composeRule.waitForIdle()

        composeRule.runOnIdle { effectKey = 2 }
        composeRule.waitForIdle()

        assertEquals(
            listOf("start:1", "cancel:1", "start:2"),
            events.take(3),
        )
    }

    @Test
    fun launchedEffect_is_cancelled_when_it_leaves_composition() {
        val events = mutableListOf<String>()
        var visible by mutableStateOf(true)

        composeRule.setContent {
            if (visible) KeyedLaunchedEffectProbe(7, events::add)
        }
        composeRule.waitForIdle()

        composeRule.runOnIdle { visible = false }
        composeRule.waitForIdle()

        assertEquals(listOf("start:7", "cancel:7"), events)
    }

    @Test
    fun rememberUpdatedState_uses_latest_value_without_restarting_effect() {
        val events = mutableListOf<String>()
        var value by mutableStateOf("original")
        composeRule.mainClock.autoAdvance = false

        composeRule.setContent {
            UpdatedValueEffectProbe(
                value = value,
                delayMillis = 1_000,
                onEvent = events::add,
            )
        }
        composeRule.runOnIdle { value = "updated" }
        composeRule.mainClock.advanceTimeBy(1_100)
        composeRule.waitForIdle()

        assertEquals(listOf("start", "delivered:updated"), events)
    }

    @Test
    fun disposableEffect_disposes_old_key_before_registering_new_one() {
        val events = mutableListOf<String>()
        val registry = FakeListenerRegistry(events::add)
        var effectKey by mutableStateOf("GPS")
        var visible by mutableStateOf(true)

        composeRule.setContent {
            if (visible) DisposableEffectProbe(effectKey, registry)
        }
        composeRule.waitForIdle()
        composeRule.runOnIdle { effectKey = "Lifecycle" }
        composeRule.waitForIdle()
        composeRule.runOnIdle { visible = false }
        composeRule.waitForIdle()

        assertEquals(
            listOf(
                "register:GPS",
                "dispose:GPS",
                "register:Lifecycle",
                "dispose:Lifecycle",
            ),
            events,
        )
    }
}

