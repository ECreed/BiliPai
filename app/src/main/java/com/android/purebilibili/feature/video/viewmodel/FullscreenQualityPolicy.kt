package com.android.purebilibili.feature.video.viewmodel

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first

internal fun VideoPlaybackUiState.Success.qualityPlaybackKey(): String =
    buildPremiumAutoUpgradePlaybackKey(info.bvid, info.cid, currentAudioLang)

internal suspend fun StateFlow<VideoPlaybackUiState>.awaitFullscreenQualityPlayback(
    playbackKey: String
): VideoPlaybackUiState.Success? {
    val state = first {
        it !is VideoPlaybackUiState.Success ||
            it.qualityPlaybackKey() != playbackKey ||
            !it.isQualitySwitching
    }
    return (state as? VideoPlaybackUiState.Success)?.takeIf { it.qualityPlaybackKey() == playbackKey }
}

/** Options have already been filtered by account and device capabilities. Never infer them from
 * the fast-start DASH cache alone: the server may advertise higher tiers that need a new request. */
internal fun resolveFullscreenQualityUpgrades(
    currentQuality: Int,
    preferredQuality: Int,
    availableQualities: List<Int>,
    explicitlySelectedQuality: Int? = null,
    dataSaverActive: Boolean = false
): List<Int> {
    if (dataSaverActive) return emptyList()
    val ceiling = explicitlySelectedQuality ?: preferredQuality
    return availableQualities.filter { it <= ceiling && it > currentQuality }.sortedDescending()
}
