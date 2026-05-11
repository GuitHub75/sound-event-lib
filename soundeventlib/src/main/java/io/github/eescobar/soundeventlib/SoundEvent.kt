package io.github.eescobar.soundeventlib

/**
 * Represents a named sound event that can be registered and played.
 * Extend or create instances via [SoundEvent.custom] for app-specific events.
 */
data class SoundEvent(val name: String) {

    companion object {
        val SUCCESS = SoundEvent("SUCCESS")
        val ERROR = SoundEvent("ERROR")
        val WARNING = SoundEvent("WARNING")
        val SCAN_DETECTED = SoundEvent("SCAN_DETECTED")
        val NOTIFICATION = SoundEvent("NOTIFICATION")

        fun custom(name: String): SoundEvent = SoundEvent(name)
    }
}
