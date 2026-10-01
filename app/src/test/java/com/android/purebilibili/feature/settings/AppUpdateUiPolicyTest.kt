package com.android.purebilibili.feature.settings

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AppUpdateUiPolicyTest {

    @Test
    fun checkUpdate_withoutNewVersion_orReleaseNotesRequest_showsNoDialog() {
        assertEquals(
            AppUpdateDialogMode.NONE,
            resolveAppUpdateDialogMode(
                isUpdateAvailable = false,
                shouldOpenReleaseNotes = false
            )
        )
    }

    @Test
    fun releaseNotesRequest_withoutNewVersion_showsChangelogDialog() {
        assertEquals(
            AppUpdateDialogMode.CHANGELOG,
            resolveAppUpdateDialogMode(
                isUpdateAvailable = false,
                shouldOpenReleaseNotes = true
            )
        )
    }

    @Test
    fun newVersion_alwaysUsesUpdateDialog_evenWhenReleaseNotesRequested() {
        assertEquals(
            AppUpdateDialogMode.UPDATE_AVAILABLE,
            resolveAppUpdateDialogMode(
                isUpdateAvailable = true,
                shouldOpenReleaseNotes = true
            )
        )
    }

    @Test
    fun updateChecksAreOnlyAvailableFromSettings() {
        val sourceRoot = listOf("src/main/java", "app/src/main/java")
            .map { java.io.File(it) }.first { it.exists() }
        val appRoot = java.io.File(sourceRoot, "com/android/purebilibili")
        val activity = java.io.File(appRoot, "MainActivity.kt").readText()
        val settings = java.io.File(appRoot, "feature/settings/screen/SettingsScreen.kt").readText()
        val sections = java.io.File(appRoot, "feature/settings/ui/SettingsSections.kt").readText()

        assertFalse(activity.contains("AppUpdateChecker"))
        assertFalse(activity.contains("AppUpdateDialogHost"))
        assertFalse(sections.contains("自动检查更新"))
        assertTrue(settings.contains("AppUpdateChecker.check("))
        assertTrue(sections.contains("onClick = onCheckUpdateClick"))
    }
}
