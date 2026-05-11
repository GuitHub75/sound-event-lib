package io.github.eescobar.soundeventlib

/**
 * Immutable configuration snapshot passed to [SoundManager.init].
 * Use [SoundConfig.Builder] to construct instances.
 *
 * Default sounds for all standard [SoundEvent] values are bundled in the library.
 * Override any event with [Builder.addEvent] to use a custom sound instead.
 */
class SoundConfig private constructor(
    val enabled: Boolean,
    val volume: Float,
    val playbackMode: PlaybackMode,
    val eventMappings: Map<SoundEvent, Int>,
) {
    class Builder {
        private var enabled: Boolean = true
        private var volume: Float = 1.0f
        private var playbackMode: PlaybackMode = PlaybackMode.OVERLAP
        private val eventMappings: MutableMap<SoundEvent, Int> = mutableMapOf(
            SoundEvent.SUCCESS       to R.raw.success,
            SoundEvent.ERROR         to R.raw.error,
            SoundEvent.WARNING       to R.raw.warning,
            SoundEvent.SCAN_DETECTED to R.raw.scan_detected,
            SoundEvent.NOTIFICATION  to R.raw.notification,
        )

        fun enabled(enabled: Boolean) = apply { this.enabled = enabled }

        /** Volume between 0.0 and 1.0 */
        fun volume(volume: Float) = apply {
            require(volume in 0f..1f) { "Volume must be between 0.0 and 1.0" }
            this.volume = volume
        }

        fun playbackMode(mode: PlaybackMode) = apply { this.playbackMode = mode }

        /** Override a [SoundEvent] with a custom raw resource id (R.raw.xxx). */
        fun addEvent(event: SoundEvent, rawResId: Int) = apply {
            eventMappings[event] = rawResId
        }

        fun build(): SoundConfig = SoundConfig(
            enabled = enabled,
            volume = volume,
            playbackMode = playbackMode,
            eventMappings = eventMappings.toMap(),
        )
    }
}
