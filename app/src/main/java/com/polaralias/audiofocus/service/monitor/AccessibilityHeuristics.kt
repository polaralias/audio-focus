package com.polaralias.audiofocus.service.monitor

import com.polaralias.audiofocus.core.model.PlaybackType
import com.polaralias.audiofocus.core.model.WindowState

class AccessibilityHeuristics(
    private val screenWidth: Int,
    private val screenHeight: Int
) {
    fun determineWindowState(
        width: Int,
        height: Int
    ): WindowState {

        if (width <= 0 || height <= 0) {
            return WindowState.NOT_VISIBLE
        }

        val isFullscreen = width >= screenWidth && height >= screenHeight
        if (isFullscreen) {
            return WindowState.FOREGROUND_FULLSCREEN
        }

        val widthRatio = width.toFloat() / screenWidth.toFloat()
        val heightRatio = height.toFloat() / screenHeight.toFloat()
        return if (widthRatio <= 0.7f && heightRatio <= 0.7f) {
            WindowState.PICTURE_IN_PICTURE
        } else {
            WindowState.FOREGROUND_MINIMISED
        }
    }

    fun determinePlaybackType(
        hasVideoSurface: Boolean,
        windowState: WindowState
    ): PlaybackType {
        if (hasVideoSurface) {
            return PlaybackType.VISIBLE_VIDEO
        }

        return if (windowState == WindowState.NOT_VISIBLE) {
            PlaybackType.NONE
        } else {
            PlaybackType.AUDIO_ONLY
        }
    }
}
