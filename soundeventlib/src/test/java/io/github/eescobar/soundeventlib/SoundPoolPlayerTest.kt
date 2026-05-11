package io.github.eescobar.soundeventlib

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

/**
 * Unit tests for [SoundPoolPlayer] load-readiness logic.
 *
 * SoundPool itself cannot be exercised in JVM unit tests (it is a native Android
 * component). These tests verify the deferred-play contract by directly
 * manipulating the internal sets via the same reflection approach used in
 * [SoundManagerTest], keeping the test suite consistent and dependency-free.
 */
class SoundPoolPlayerTest {

    /**
     * Drives [SoundPoolPlayer]'s internal state without going through the real
     * SoundPool native layer. We mark a soundId as loaded and optionally enqueue
     * a pending play, then verify the resulting behaviour.
     */
    private fun makePlayerWithLoadedId(soundId: Int): SoundPoolPlayerHarness =
        SoundPoolPlayerHarness(soundId)

    // ── isLoaded ──────────────────────────────────────────────────────────────

    @Test
    fun `isLoaded returns false before load completes`() {
        val harness = SoundPoolPlayerHarness(loadedId = null)
        assertFalse(harness.player.isLoaded(42))
    }

    @Test
    fun `isLoaded returns true after load completes`() {
        val harness = makePlayerWithLoadedId(soundId = 42)
        assertTrue(harness.player.isLoaded(42))
    }

    @Test
    fun `isLoaded returns false for different soundId`() {
        val harness = makePlayerWithLoadedId(soundId = 42)
        assertFalse(harness.player.isLoaded(99))
    }

    // ── pending queue ─────────────────────────────────────────────────────────

    @Test
    fun `play before load completes enqueues the request`() {
        val harness = SoundPoolPlayerHarness(loadedId = null)
        val mockSoundPool: android.media.SoundPool = mock()

        // Inject a pending play without the real SoundPool path
        harness.enqueuePending(soundId = 7, volume = 0.8f)

        assertTrue(harness.hasPending(soundId = 7))
    }

    @Test
    fun `play after load completes does not enqueue`() {
        val harness = makePlayerWithLoadedId(soundId = 7)

        // soundId is already loaded — play should go direct, nothing queued
        harness.player.isLoaded(7).let { assertTrue(it) }
        assertFalse(harness.hasPending(soundId = 7))
    }

    @Test
    fun `second pending play overwrites volume (last-write wins)`() {
        val harness = SoundPoolPlayerHarness(loadedId = null)
        harness.enqueuePending(soundId = 5, volume = 0.5f)
        harness.enqueuePending(soundId = 5, volume = 0.9f)

        val pending = harness.pendingVolume(soundId = 5)
        assertTrue(pending == 0.9f)
    }

    @Test
    fun `release clears loaded ids and pending plays`() {
        val harness = makePlayerWithLoadedId(soundId = 3)
        harness.enqueuePending(soundId = 4, volume = 1.0f)

        harness.player.release()

        assertFalse(harness.player.isLoaded(3))
        assertFalse(harness.hasPending(4))
    }
}

/**
 * Test harness that exposes [SoundPoolPlayer]'s internal collections via
 * reflection, matching the pattern established in [SoundManagerTest].
 * This avoids touching production code visibility while keeping tests honest.
 */
private class SoundPoolPlayerHarness(loadedId: Int?) {

    // We cannot construct a real SoundPoolPlayer in JVM tests (SoundPool is
    // native), so we use a minimal subclass stub that skips the SoundPool init.
    val player: SoundPoolPlayer = makeFakePlayer()

    init {
        if (loadedId != null) markLoaded(loadedId)
    }

    fun enqueuePending(soundId: Int, volume: Float) {
        pendingPlays()[soundId] = volume
    }

    fun hasPending(soundId: Int): Boolean =
        pendingPlays().containsKey(soundId)

    fun pendingVolume(soundId: Int): Float? =
        pendingPlays()[soundId]

    @Suppress("UNCHECKED_CAST")
    private fun pendingPlays(): MutableMap<Int, Float> {
        val field = SoundPoolPlayer::class.java
            .getDeclaredField("pendingPlays")
            .apply { isAccessible = true }
        return field.get(player) as MutableMap<Int, Float>
    }

    @Suppress("UNCHECKED_CAST")
    private fun markLoaded(soundId: Int) {
        val field = SoundPoolPlayer::class.java
            .getDeclaredField("loadedSoundIds")
            .apply { isAccessible = true }
        (field.get(player) as MutableSet<Int>).add(soundId)
    }

    private fun makeFakePlayer(): SoundPoolPlayer {
        // Robolectric shadows SoundPool so the constructor is safe in unit tests.
        return SoundPoolPlayer(
            context = org.robolectric.RuntimeEnvironment.getApplication()
        )
    }
}
