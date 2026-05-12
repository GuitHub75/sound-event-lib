package io.github.eescobar.soundeventlib

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool

/**
 * [SoundPlayer] backed by [SoundPool].
 * Suitable for short UI sounds (< 1 MB, < ~5 s).
 *
 * ## Load lifecycle
 * [SoundPool.load] is asynchronous: it returns a soundId immediately but the
 * audio buffer is not ready until [SoundPool.OnLoadCompleteListener] fires.
 * Calling [SoundPool.play] before the buffer is ready produces a silent no-op
 * with no error or exception from the framework.
 *
 * This class handles that by:
 * 1. Tracking each soundId as "loading" in [loadedSoundIds] (absent = not ready).
 * 2. Queuing any [play] call that arrives before the buffer is ready.
 * 3. Draining the pending queue automatically once the load callback fires.
 */
internal class SoundPoolPlayer(
    context: Context,
    maxStreams: Int = 5,
) : SoundPlayer {

    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(maxStreams)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private val appContext = context.applicationContext

    // soundIds whose buffers have completed loading.
    private val loadedSoundIds = mutableSetOf<Int>()

    // Plays deferred until their soundId finishes loading: soundId → volume.
    private val pendingPlays = mutableMapOf<Int, Float>()

    init {
        soundPool.setOnLoadCompleteListener { _, sampleId, status ->
            if (status == 0) {
                loadedSoundIds.add(sampleId)
                pendingPlays.remove(sampleId)?.let { volume ->
                    soundPool.play(sampleId, volume, volume, 1, 0, 1f)
                }
            }
        }
    }

    override fun load(rawResId: Int): Int =
        soundPool.load(appContext, rawResId, 1)

    override fun isLoaded(soundId: Int): Boolean =
        soundId in loadedSoundIds

    /**
     * Plays [soundId] at [volume].
     * If the buffer is not yet loaded, the request is queued and replayed
     * automatically when [OnLoadCompleteListener] fires for that soundId.
     * A subsequent call before load completes overwrites the pending volume
     * (last-write wins — avoids stacking duplicate plays for burst events).
     */
    override fun play(soundId: Int, volume: Float) {
        if (soundId in loadedSoundIds) {
            soundPool.play(soundId, volume, volume, 1, 0, 1f)
        } else {
            pendingPlays[soundId] = volume
        }
    }

    override fun release() {
        loadedSoundIds.clear()
        pendingPlays.clear()
        soundPool.release()
    }
}
