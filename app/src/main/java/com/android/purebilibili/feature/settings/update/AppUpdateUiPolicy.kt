package com.android.purebilibili.feature.settings

internal enum class AppUpdateDialogMode {
    NONE,
    UPDATE_AVAILABLE,
    CHANGELOG
}

internal fun resolveAppUpdateDialogMode(
    isUpdateAvailable: Boolean,
    shouldOpenReleaseNotes: Boolean
): AppUpdateDialogMode {
    return when {
        isUpdateAvailable -> AppUpdateDialogMode.UPDATE_AVAILABLE
        shouldOpenReleaseNotes -> AppUpdateDialogMode.CHANGELOG
        else -> AppUpdateDialogMode.NONE
    }
}
