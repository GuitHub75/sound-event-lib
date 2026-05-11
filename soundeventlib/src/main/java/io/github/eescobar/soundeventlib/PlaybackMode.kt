package io.github.eescobar.soundeventlib

/** Controls how concurrent sound play requests are handled. */
enum class PlaybackMode {
    /** New sounds play on top of currently playing sounds (SoundPool streams). */
    OVERLAP,

    /** New sounds are enqueued; each waits for the previous to finish. */
    QUEUE,
}
