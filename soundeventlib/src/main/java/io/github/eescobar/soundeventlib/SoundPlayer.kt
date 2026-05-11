package io.github.eescobar.soundeventlib

/** Abstraction over the actual audio playback engine. */
internal interface SoundPlayer {
    fun load(rawResId: Int): Int
    fun play(soundId: Int, volume: Float)

    /**
     * Returns true when [soundId] has finished loading and is ready to play.
     * Callers must check this before invoking [play] to avoid silent no-ops
     * caused by SoundPool's asynchronous buffer loading.
     */
    fun isLoaded(soundId: Int): Boolean

    fun release()
}
