package io.github.eescobar.soundeventlib

/**
 * Maps [SoundEvent] keys to loaded SoundPool sound IDs.
 * Keeps event-to-sound mapping fully decoupled from playback.
 */
internal class EventRegistry {

    private val registry: MutableMap<SoundEvent, Int> = mutableMapOf()

    fun register(event: SoundEvent, soundId: Int) {
        registry[event] = soundId
    }

    fun soundIdFor(event: SoundEvent): Int? = registry[event]

    fun clear() = registry.clear()
}
