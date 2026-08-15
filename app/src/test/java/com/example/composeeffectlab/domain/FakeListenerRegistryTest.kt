package com.example.composeeffectlab.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FakeListenerRegistryTest {
    @Test
    fun `register and dispose report lifecycle in order`() {
        val events = mutableListOf<String>()
        val registry = FakeListenerRegistry(events::add)

        val registration = registry.register("GPS")

        assertEquals(setOf("GPS"), registry.activeKeys)
        registration.dispose()

        assertTrue(registry.activeKeys.isEmpty())
        assertEquals(listOf("register:GPS", "dispose:GPS"), events)
    }

    @Test
    fun `dispose is idempotent`() {
        val events = mutableListOf<String>()
        val registration = FakeListenerRegistry(events::add).register("Lifecycle")

        registration.dispose()
        registration.dispose()

        assertEquals(listOf("register:Lifecycle", "dispose:Lifecycle"), events)
    }
}

