package io.github.eescobar.soundeventlib

import java.util.LinkedList
import java.util.Queue

/** Strategy interface for deciding how a play request is dispatched. */
internal interface PlaybackStrategy {
    fun execute(soundId: Int, volume: Float, player: SoundPlayer)
    fun release() {}
}

/** Plays sounds immediately, overlapping any currently playing audio. */
internal class OverlapStrategy : PlaybackStrategy {
    override fun execute(soundId: Int, volume: Float, player: SoundPlayer) {
        player.play(soundId, volume)
    }
}

/**
 * Enqueues play requests and fires the next only after the previous finishes.
 * Uses a fixed playback duration estimate; for precise sequencing, prefer
 * a MediaPlayer-based implementation with completion callbacks.
 */
internal class QueueStrategy(
    private val durationMs: Long = 1_000L,
) : PlaybackStrategy {

    private val queue: Queue<Pair<Int, Float>> = LinkedList()
    private var isPlaying = false

    @Synchronized
    override fun execute(soundId: Int, volume: Float, player: SoundPlayer) {
        queue.offer(soundId to volume)
        if (!isPlaying) drainQueue(player)
    }

    private fun drainQueue(player: SoundPlayer) {
        val next = queue.poll() ?: run { isPlaying = false; return }
        isPlaying = true
        player.play(next.first, next.second)
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            drainQueue(player)
        }, durationMs)
    }

    @Synchronized
    override fun release() {
        queue.clear()
        isPlaying = false
    }
}
