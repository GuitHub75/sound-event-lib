package io.github.eescobar.soundeventlib

import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

class SoundManagerTest {

    private val mockPlayer: SoundPlayer = mock()
    private val mockStrategy: PlaybackStrategy = mock()

    // Access internal state via reflection for testing purposes
    private fun injectInternals(player: SoundPlayer, strategy: PlaybackStrategy, config: SoundConfig) {
        val cls = SoundManager::class.java

        fun setField(name: String, value: Any?) {
            val field = cls.getDeclaredField(name).apply { isAccessible = true }
            field.set(SoundManager, value)
        }

        val registry = EventRegistry()
        config.eventMappings.forEach { (event, _) ->
            registry.register(event, event.name.hashCode())
        }

        setField("player", player)
        setField("strategy", strategy)
        setField("registry", registry)
        setField("config", config)
    }

    @Before
    fun setUp() {
        SoundManager.release()
    }

    @After
    fun tearDown() {
        SoundManager.release()
    }

    @Test
    fun `play dispatches to strategy when enabled`() {
        val config = SoundConfig.Builder()
            .addEvent(SoundEvent.SUCCESS, 1)
            .build()
        injectInternals(mockPlayer, mockStrategy, config)

        SoundManager.play(SoundEvent.SUCCESS)

        verify(mockStrategy).execute(any(), eq(1.0f), eq(mockPlayer))
    }

    @Test
    fun `play does nothing when globally disabled`() {
        val config = SoundConfig.Builder()
            .enabled(false)
            .addEvent(SoundEvent.SUCCESS, 1)
            .build()
        injectInternals(mockPlayer, mockStrategy, config)

        SoundManager.play(SoundEvent.SUCCESS)

        verify(mockStrategy, never()).execute(any(), any(), any())
    }

    @Test
    fun `play does nothing for unregistered event`() {
        val config = SoundConfig.Builder().build()
        injectInternals(mockPlayer, mockStrategy, config)

        SoundManager.play(SoundEvent.custom("UNREGISTERED_EVENT"))

        verify(mockStrategy, never()).execute(any(), any(), any())
    }

    @Test
    fun `setEnabled false disables playback`() {
        val config = SoundConfig.Builder()
            .addEvent(SoundEvent.SUCCESS, 1)
            .build()
        injectInternals(mockPlayer, mockStrategy, config)

        SoundManager.setEnabled(false)
        SoundManager.play(SoundEvent.SUCCESS)

        verify(mockStrategy, never()).execute(any(), any(), any())
    }

    @Test
    fun `isInitialized returns false before init`() {
        assertFalse(SoundManager.isInitialized)
    }

    @Test
    fun `isInitialized returns true after injecting player`() {
        val config = SoundConfig.Builder().build()
        injectInternals(mockPlayer, mockStrategy, config)
        assertTrue(SoundManager.isInitialized)
    }

    @Test
    fun `release clears state`() {
        val config = SoundConfig.Builder().build()
        injectInternals(mockPlayer, mockStrategy, config)

        SoundManager.release()

        assertFalse(SoundManager.isInitialized)
        verify(mockPlayer).release()
        verify(mockStrategy).release()
    }
}
