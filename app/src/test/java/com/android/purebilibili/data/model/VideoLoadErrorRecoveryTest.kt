package com.android.purebilibili.data.model

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class VideoLoadErrorRecoveryTest {
    @Test
    fun localCooldownAllowsManualRecoveryButNotAutomaticRetries() {
        listOf(VideoLoadError.GlobalCooldown(120_000, 8), VideoLoadError.RateLimited(60_000, "BV1")).forEach {
            assertTrue(it.canRetryManually())
            assertFalse(it.isRetryable())
        }
        assertFalse(VideoLoadError.VideoNotFound.canRetryManually())
    }

    @Test
    fun normalNetworkFailuresDoNotLockOtherVideos() {
        listOf(
            VideoLoadError.Timeout,
            VideoLoadError.fromException(java.net.SocketTimeoutException()),
            VideoLoadError.VideoNotFound,
            VideoLoadError.UnknownError(java.util.concurrent.CancellationException())
        ).forEach { assertFalse(it.shouldTriggerPlaybackCooldown()) }
        assertTrue(VideoLoadError.WbiSignatureError.shouldTriggerPlaybackCooldown())
        assertTrue(VideoLoadError.PlayUrlEmpty.shouldTriggerPlaybackCooldown())
        assertTrue(VideoLoadError.ApiError(-412, "").shouldTriggerPlaybackCooldown())
    }
}
