package com.polaralias.audiofocus.core.logic

import android.media.session.PlaybackState
import com.polaralias.audiofocus.core.model.MediaAction
import com.polaralias.audiofocus.core.model.TargetApp
import com.polaralias.audiofocus.service.monitor.MediaSessionMonitor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaControlClient @Inject constructor(
    private val mediaSessionMonitor: MediaSessionMonitor,
    private val capabilitiesResolver: MediaControlCapabilitiesResolver
) {
    fun sendAction(targetApp: TargetApp, action: MediaAction) {
        val controller = mediaSessionMonitor.getController(targetApp) ?: return
        val transportControls = controller.transportControls
        val actions = controller.playbackState?.actions ?: 0L
        val capabilities = capabilitiesResolver.resolve(actions)

        when (action) {
            is MediaAction.Play -> if (capabilities.canPlay) transportControls.play()
            is MediaAction.Pause -> if (capabilities.canPause) transportControls.pause()
            is MediaAction.SkipForward -> {
                val state = controller.playbackState
                when {
                    capabilities.canSeek && state != null -> {
                        val current = state.position
                        transportControls.seekTo(current + 10000)
                    }
                    actions and PlaybackState.ACTION_SKIP_TO_NEXT != 0L -> {
                        transportControls.skipToNext()
                    }
                    actions and PlaybackState.ACTION_FAST_FORWARD != 0L -> {
                        transportControls.fastForward()
                    }
                }
            }
            is MediaAction.SkipBackward -> {
                val state = controller.playbackState
                when {
                    capabilities.canSeek && state != null -> {
                        val current = state.position
                        val newPos = (current - 10000).coerceAtLeast(0)
                        transportControls.seekTo(newPos)
                    }
                    actions and PlaybackState.ACTION_SKIP_TO_PREVIOUS != 0L -> {
                        transportControls.skipToPrevious()
                    }
                    actions and PlaybackState.ACTION_REWIND != 0L -> {
                        transportControls.rewind()
                    }
                }
            }
            is MediaAction.Seek -> if (capabilities.canSeek) transportControls.seekTo(action.position)
        }
    }
}
