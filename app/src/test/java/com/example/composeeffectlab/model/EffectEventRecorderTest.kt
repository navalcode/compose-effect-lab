package com.example.composeeffectlab.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EffectEventRecorderTest {
    @Test
    fun `record assigns ordered identifiers and preserves event data`() {
        val snapshots = mutableListOf<List<EffectEvent>>()
        val recorder = EffectEventRecorder(snapshots::add)

        val first = recorder.record("LaunchedEffect", "start key=1", EventKind.START)
        val second = recorder.record("LaunchedEffect", "cancel key=1", EventKind.DISPOSE)

        assertEquals(1L, first.id)
        assertEquals(2L, second.id)
        assertEquals(listOf(first, second), recorder.events)
        assertEquals(recorder.events, snapshots.last())
    }

    @Test
    fun `clear removes events and restarts identifiers`() {
        val recorder = EffectEventRecorder()
        recorder.record("test", "before")

        recorder.clear()
        val afterClear = recorder.record("test", "after")

        assertTrue(recorder.events.single().message == "after")
        assertEquals(1L, afterClear.id)
    }
}

