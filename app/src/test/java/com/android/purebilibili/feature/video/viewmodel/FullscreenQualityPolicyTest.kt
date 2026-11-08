package com.android.purebilibili.feature.video.viewmodel

import com.android.purebilibili.data.model.response.ViewInfo
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

class FullscreenQualityPolicyTest {
    @Test
    fun fullscreenUpgradesFastStart1080pToAdvertisedHighestTier() {
        assertEquals(listOf(116), resolveFullscreenQualityUpgrades(80, 125, listOf(116, 80, 64)))
        assertEquals(
            listOf(120, 116),
            resolveFullscreenQualityUpgrades(80, 120, listOf(116, 125, 80, 120))
        )
    }

    @Test
    fun fullscreenPreservesAnExplicitLowerChoiceAndHonorsHigherChoiceAfterFallback() {
        assertEquals(
            emptyList(),
            resolveFullscreenQualityUpgrades(80, 125, listOf(125, 116, 80), explicitlySelectedQuality = 80)
        )
        assertEquals(
            listOf(116),
            resolveFullscreenQualityUpgrades(80, 125, listOf(125, 116, 80), explicitlySelectedQuality = 116)
        )
    }

    @Test
    fun fullscreenDoesNotForceUnavailableQualityOrOverrideDataSaver() {
        assertEquals(emptyList(), resolveFullscreenQualityUpgrades(80, 125, listOf(80, 64)))
        assertEquals(emptyList(), resolveFullscreenQualityUpgrades(116, 116, listOf(116, 80)))
        assertEquals(emptyList(), resolveFullscreenQualityUpgrades(80, 125, listOf(125, 80), dataSaverActive = true))
    }

    @Test
    fun fullscreenWaitsForInFlightSwitchAndDiscardsItWhenThePartChanges() = runTest {
        val switching = VideoPlaybackUiState.Success(
            info = ViewInfo(bvid = "BV1", cid = 1L),
            playUrl = "https://example.test/video",
            isQualitySwitching = true
        )
        val state = MutableStateFlow<VideoPlaybackUiState>(switching)
        val key = switching.qualityPlaybackKey()
        val pending = async(start = CoroutineStart.UNDISPATCHED) { state.awaitFullscreenQualityPlayback(key) }
        assertFalse(pending.isCompleted)

        val settled = switching.copy(isQualitySwitching = false, currentQuality = 80)
        state.value = settled
        assertEquals(settled, pending.await())

        state.value = switching
        val stale = async(start = CoroutineStart.UNDISPATCHED) { state.awaitFullscreenQualityPlayback(key) }
        state.value = settled.copy(info = settled.info.copy(cid = 2L))
        assertNull(stale.await())
    }
}
