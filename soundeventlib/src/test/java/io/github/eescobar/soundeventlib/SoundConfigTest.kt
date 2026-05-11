package io.github.eescobar.soundeventlib

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SoundConfigTest {

    @Test
    fun `default config has correct defaults`() {
        val config = SoundConfig.Builder().build()
        assertTrue(config.enabled)
        assertEquals(1.0f, config.volume, 0.001f)
        assertEquals(PlaybackMode.OVERLAP, config.playbackMode)
        assertEquals(5, config.eventMappings.size)
        assertTrue(config.eventMappings.containsKey(SoundEvent.SUCCESS))
        assertTrue(config.eventMappings.containsKey(SoundEvent.ERROR))
        assertTrue(config.eventMappings.containsKey(SoundEvent.WARNING))
        assertTrue(config.eventMappings.containsKey(SoundEvent.SCAN_DETECTED))
        assertTrue(config.eventMappings.containsKey(SoundEvent.NOTIFICATION))
    }

    @Test
    fun `builder sets all fields`() {
        val config = SoundConfig.Builder()
            .enabled(false)
            .volume(0.5f)
            .playbackMode(PlaybackMode.QUEUE)
            .addEvent(SoundEvent.SUCCESS, 101)
            .addEvent(SoundEvent.ERROR, 102)
            .build()

        assertFalse(config.enabled)
        assertEquals(0.5f, config.volume, 0.001f)
        assertEquals(PlaybackMode.QUEUE, config.playbackMode)
        assertEquals(101, config.eventMappings[SoundEvent.SUCCESS])
        assertEquals(102, config.eventMappings[SoundEvent.ERROR])
    }

    @Test(expected = IllegalArgumentException::class)
    fun `volume above 1 throws`() {
        SoundConfig.Builder().volume(1.5f)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `negative volume throws`() {
        SoundConfig.Builder().volume(-0.1f)
    }
}
