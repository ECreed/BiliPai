package com.android.purebilibili.feature.video.viewmodel

/** Options have already been filtered by account and device capabilities. Never infer them from
 * the fast-start DASH cache alone: the server may advertise higher tiers that need a new request. */
internal fun resolveFullscreenQualityUpgrade(
    currentQuality: Int,
    preferredQuality: Int,
    availableQualities: List<Int>,
    explicitlySelectedQuality: Int? = null,
    dataSaverActive: Boolean = false
): Int? {
    if (dataSaverActive) return null
    val ceiling = explicitlySelectedQuality ?: preferredQuality
    return availableQualities.filter { it <= ceiling && it > currentQuality }.maxOrNull()
}
