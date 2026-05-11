package io.github.eescobar.soundeventlib

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class EventRegistryTest {

    private val registry = EventRegistry()

    @Test
    fun `returns soundId for registered event`() {
        registry.register(SoundEvent.SUCCESS, 42)
        assertEquals(42, registry.soundIdFor(SoundEvent.SUCCESS))
    }

    @Test
    fun `returns null for unregistered event`() {
        assertNull(registry.soundIdFor(SoundEvent.ERROR))
    }

    @Test
    fun `clear removes all entries`() {
        registry.register(SoundEvent.SUCCESS, 1)
        registry.register(SoundEvent.ERROR, 2)
        registry.clear()
        assertNull(registry.soundIdFor(SoundEvent.SUCCESS))
        assertNull(registry.soundIdFor(SoundEvent.ERROR))
    }

    @Test
    fun `overwriting event updates soundId`() {
        registry.register(SoundEvent.SUCCESS, 1)
        registry.register(SoundEvent.SUCCESS, 99)
        assertEquals(99, registry.soundIdFor(SoundEvent.SUCCESS))
    }
}
