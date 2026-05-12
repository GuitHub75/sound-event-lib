package io.github.eescobar.soundeventlib

import android.content.Context

/**
 * Entry point for the sound event library.
 *
 * Typical setup:
 * ```kotlin
 * SoundManager.init(
 *     context,
 *     SoundConfig.Builder()
 *         .addEvent(SoundEvent.SUCCESS, R.raw.success)
 *         .addEvent(SoundEvent.ERROR,   R.raw.error)
 *         .volume(0.8f)
 *         .build()
 * )
 *
 * // Later, anywhere in your code:
 * SoundManager.play(SoundEvent.SUCCESS)
 * ```
 */
object SoundManager {

    private var player: SoundPlayer? = null
    private var registry: EventRegistry? = null
    private var strategy: PlaybackStrategy? = null

    // Mutable runtime state — kept separate so setEnabled/setVolume never
    // rebuild the config and accidentally reset the loaded sound mappings.
    private var enabled: Boolean = true
    private var volume: Float = 1.0f

    /**
     * Initializes the manager. Safe to call from Application.onCreate().
     * Calling again releases previous resources and reinitializes.
     */
    fun init(context: Context, soundConfig: SoundConfig) {
        release()

        enabled = soundConfig.enabled
        volume = soundConfig.volume

        val soundPlayer = SoundPoolPlayer(context)
        player = soundPlayer

        val eventRegistry = EventRegistry()
        registry = eventRegistry

        soundConfig.eventMappings.forEach { (event, rawResId) ->
            val soundId = soundPlayer.load(rawResId)
            eventRegistry.register(event, soundId)
        }

        strategy = when (soundConfig.playbackMode) {
            PlaybackMode.OVERLAP -> OverlapStrategy()
            PlaybackMode.QUEUE   -> QueueStrategy()
        }
    }

    /**
     * Plays the sound associated with [event].
     * Does nothing if sounds are globally disabled or the event is not registered.
     */
    fun play(event: SoundEvent) {
        if (!enabled) return
        val soundId = registry?.soundIdFor(event) ?: return
        strategy?.execute(soundId, volume, player ?: return)
    }

    /** Registers an additional event at runtime. Requires [init] to have been called first. */
    fun register(event: SoundEvent, rawResId: Int) {
        val soundId = player?.load(rawResId) ?: return
        registry?.register(event, soundId)
    }

    /** Enables or disables all sound playback without releasing resources. */
    fun setEnabled(enabled: Boolean) {
        this.enabled = enabled
    }

    /** Adjusts global playback volume (0.0 – 1.0). */
    fun setVolume(volume: Float) {
        this.volume = volume.coerceIn(0f, 1f)
    }

    /** Returns true if the manager has been initialized. */
    val isInitialized: Boolean get() = player != null

    /**
     * Releases all audio resources. Call from Application.onTerminate() or
     * wherever the host component is destroyed.
     */
    fun release() {
        strategy?.release()
        player?.release()
        registry?.clear()
        player = null
        registry = null
        strategy = null
        enabled = true
        volume = 1.0f
    }
}
