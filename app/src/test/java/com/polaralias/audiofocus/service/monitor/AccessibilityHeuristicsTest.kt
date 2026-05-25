package com.polaralias.audiofocus.service.monitor

import com.polaralias.audiofocus.core.model.PlaybackType
import com.polaralias.audiofocus.core.model.WindowState
import org.junit.Assert.assertEquals
import org.junit.Test

class AccessibilityHeuristicsTest {

    private val heuristics = AccessibilityHeuristics(screenWidth = 1000, screenHeight = 2000)

    @Test
    fun `fullscreen bounds map to foreground fullscreen`() {
        val state = heuristics.determineWindowState(width = 1000, height = 2000)

        assertEquals(WindowState.FOREGROUND_FULLSCREEN, state)
    }

    @Test
    fun `small floating bounds map to picture in picture`() {
        val state = heuristics.determineWindowState(width = 280, height = 500)

        assertEquals(WindowState.PICTURE_IN_PICTURE, state)
    }

    @Test
    fun `visible non fullscreen bounds map to foreground minimised`() {
        val state = heuristics.determineWindowState(width = 1000, height = 1300)

        assertEquals(WindowState.FOREGROUND_MINIMISED, state)
    }

    @Test
    fun `missing video surface in visible window maps to audio only`() {
        val playbackType = heuristics.determinePlaybackType(
            hasVideoSurface = false,
            windowState = WindowState.FOREGROUND_MINIMISED
        )

        assertEquals(PlaybackType.AUDIO_ONLY, playbackType)
    }

    @Test
    fun `missing visible window maps to no playback type`() {
        val playbackType = heuristics.determinePlaybackType(
            hasVideoSurface = false,
            windowState = WindowState.NOT_VISIBLE
        )

        assertEquals(PlaybackType.NONE, playbackType)
    }
}
