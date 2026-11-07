package com.android.purebilibili.feature.video.viewmodel

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FullscreenQualityPolicyTest {
    @Test
    fun fullscreenUpgradesFastStart1080pToAdvertisedHighestTier() {
        assertEquals(116, resolveFullscreenQualityUpgrade(80, 125, listOf(116, 80, 64)))
        assertEquals(120, resolveFullscreenQualityUpgrade(80, 120, listOf(125, 120, 116, 80)))
    }

    @Test
    fun fullscreenPreservesAnExplicitLowerChoiceAndHonorsHigherChoiceAfterFallback() {
        assertNull(resolveFullscreenQualityUpgrade(80, 125, listOf(125, 116, 80), explicitlySelectedQuality = 80))
        assertEquals(116, resolveFullscreenQualityUpgrade(80, 125, listOf(125, 116, 80), explicitlySelectedQuality = 116))
    }

    @Test
    fun fullscreenDoesNotForceUnavailableQualityOrOverrideDataSaver() {
        assertNull(resolveFullscreenQualityUpgrade(80, 125, listOf(80, 64)))
        assertNull(resolveFullscreenQualityUpgrade(116, 116, listOf(116, 80)))
        assertNull(resolveFullscreenQualityUpgrade(80, 125, listOf(125, 80), dataSaverActive = true))
    }
}
