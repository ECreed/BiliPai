@file:androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)

package com.android.purebilibili.feature.video.usecase

import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.SeekParameters
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class VideoPlaybackUserSeekPolicyTest {
    @Test
    fun `small backward and forward user seeks are exact without changing the fast seek preference`() {
        listOf(58_000L, 62_000L).forEach { target ->
            val player = mockk<ExoPlayer>(relaxed = true)
            every { player.currentPosition } returns 60_000L
            every { player.seekParameters } returns SeekParameters.CLOSEST_SYNC
            every { player.playbackState } returns Player.STATE_READY
            every { player.playWhenReady } returns false

            seekPlayerFromUserAction(player, target)

            verifyOrder {
                player.setSeekParameters(SeekParameters.EXACT)
                player.seekTo(target)
                player.setSeekParameters(SeekParameters.CLOSEST_SYNC)
            }
        }
    }


    @Test
    fun `shouldResumePlaybackAfterUserSeek keeps playback intent when playWhenReady was true`() {
        assertTrue(
            shouldResumePlaybackAfterUserSeek(
                playWhenReadyBeforeSeek = true,
                playbackStateBeforeSeek = Player.STATE_READY
            )
        )
    }

    @Test
    fun `shouldResumePlaybackAfterUserSeek keeps paused state when playWhenReady was false`() {
        assertFalse(
            shouldResumePlaybackAfterUserSeek(
                playWhenReadyBeforeSeek = false,
                playbackStateBeforeSeek = Player.STATE_READY
            )
        )
    }

    @Test
    fun `shouldResumePlaybackAfterUserSeek resumes when user scrubs away from ended state`() {
        assertTrue(
            shouldResumePlaybackAfterUserSeek(
                playWhenReadyBeforeSeek = false,
                playbackStateBeforeSeek = Player.STATE_ENDED
            )
        )
    }

    @Test
    fun `seekPlayerFromUserAction resumes playback after seek when player was playing`() {
        val player = mockk<Player>(relaxed = true)
        every { player.playWhenReady } returns true
        every { player.playbackState } returns Player.STATE_READY
        every { player.mediaItemCount } returns 1

        seekPlayerFromUserAction(player, 12_345L)

        verify(exactly = 1) { player.playWhenReady = true }
        verify(exactly = 1) { player.seekTo(12_345L) }
        verify(exactly = 1) { player.play() }
    }

    @Test
    fun `seekPlayerFromUserAction keeps player paused after seek when playback was paused`() {
        val player = mockk<Player>(relaxed = true)
        every { player.playWhenReady } returns false
        every { player.playbackState } returns Player.STATE_READY

        seekPlayerFromUserAction(player, 12_345L)

        verify(exactly = 0) { player.playWhenReady = any() }
        verify(exactly = 1) { player.seekTo(12_345L) }
        verify(exactly = 0) { player.play() }
    }

    @Test
    fun `seekPlayerFromUserAction honors explicit resume override from scrubbing session`() {
        val player = mockk<Player>(relaxed = true)
        every { player.playWhenReady } returns false
        every { player.playbackState } returns Player.STATE_READY
        every { player.mediaItemCount } returns 1

        seekPlayerFromUserAction(
            player = player,
            positionMs = 12_345L,
            shouldResumePlaybackOverride = true
        )

        verify(exactly = 1) { player.playWhenReady = true }
        verify(exactly = 1) { player.seekTo(12_345L) }
        verify(exactly = 1) { player.play() }
    }
}
