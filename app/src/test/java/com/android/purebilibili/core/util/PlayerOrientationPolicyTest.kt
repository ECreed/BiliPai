package com.android.purebilibili.core.util

import android.content.pm.ActivityInfo
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PlayerOrientationPolicyTest {
    @Test
    fun `phone non video pages reject sensor and delayed cleanup rotation`() {
        listOf(
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED,
            ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR,
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE,
        ).forEach { request ->
            assertEquals(
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,
                resolveAppRequestedOrientation(request, false, 363, false, false),
            )
        }
    }

    @Test
    fun `video fullscreen and windowed playback retain their orientation policy`() {
        val landscape = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
        assertEquals(landscape, resolveAppRequestedOrientation(landscape, true, 363, false, false))
        assertEquals(landscape, resolveAppRequestedOrientation(landscape, false, 363, true, false))
        assertEquals(landscape, resolveAppRequestedOrientation(landscape, false, 363, false, true))
        assertEquals(
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED,
            resolveAppRequestedOrientation(landscape, false, 800, false, false),
        )
    }

    @Test
    fun `physical orientation remains available below 600dp`() {
        assertTrue(
            shouldRequestPhysicalPlayerOrientation(
                smallestScreenWidthDp = 599,
                platformIgnoresLargeScreenOrientationRequests = true,
            )
        )
    }

    @Test
    fun `pre Android 16 tablets retain direct fullscreen rotation`() {
        assertTrue(
            shouldRequestPhysicalPlayerOrientation(
                smallestScreenWidthDp = 600,
                platformIgnoresLargeScreenOrientationRequests = false,
            )
        )
        assertTrue(
            shouldRequestPhysicalPlayerOrientation(
                smallestScreenWidthDp = 720,
                platformIgnoresLargeScreenOrientationRequests = false,
            )
        )
    }

    @Test
    fun `Android 16 plus large screens use platform adaptive orientation`() {
        assertFalse(
            shouldRequestPhysicalPlayerOrientation(
                smallestScreenWidthDp = 600,
                platformIgnoresLargeScreenOrientationRequests = true,
            )
        )
        assertFalse(
            shouldRequestPhysicalPlayerOrientation(
                smallestScreenWidthDp = 720,
                platformIgnoresLargeScreenOrientationRequests = true,
            )
        )
    }
}
