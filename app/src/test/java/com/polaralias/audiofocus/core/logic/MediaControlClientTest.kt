package com.polaralias.audiofocus.core.logic

import android.media.session.MediaController
import android.media.session.PlaybackState
import com.polaralias.audiofocus.core.model.MediaAction
import com.polaralias.audiofocus.core.model.TargetApp
import com.polaralias.audiofocus.service.monitor.MediaSessionMonitor
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.Test

class MediaControlClientTest {

    private val mediaSessionMonitor = mockk<MediaSessionMonitor>()
    private val controller = mockk<MediaController>()
    private val transportControls = mockk<MediaController.TransportControls>(relaxed = true)
    private val client = MediaControlClient(mediaSessionMonitor, MediaControlCapabilitiesResolver())

    @Test
    fun `unsupported seek does not send command`() {
        val playbackState = mockk<PlaybackState>()
        every { playbackState.actions } returns PlaybackState.ACTION_PLAY
        every { controller.playbackState } returns playbackState
        every { controller.transportControls } returns transportControls
        every { mediaSessionMonitor.getController(TargetApp.YOUTUBE) } returns controller

        client.sendAction(TargetApp.YOUTUBE, MediaAction.Seek(5_000))

        verify(exactly = 0) { transportControls.seekTo(any()) }
    }

    @Test
    fun `supported seek sends seek command`() {
        val playbackState = mockk<PlaybackState>()
        every { playbackState.actions } returns PlaybackState.ACTION_SEEK_TO
        every { controller.playbackState } returns playbackState
        every { controller.transportControls } returns transportControls
        every { mediaSessionMonitor.getController(TargetApp.YOUTUBE) } returns controller

        client.sendAction(TargetApp.YOUTUBE, MediaAction.Seek(5_000))

        verify { transportControls.seekTo(5_000) }
    }

    @Test
    fun `skip forward prefers seek when seeking is supported`() {
        val playbackState = mockk<PlaybackState>()
        every { playbackState.actions } returns PlaybackState.ACTION_SEEK_TO
        every { playbackState.position } returns 12_000L
        every { controller.playbackState } returns playbackState
        every { controller.transportControls } returns transportControls
        every { mediaSessionMonitor.getController(TargetApp.YOUTUBE) } returns controller

        client.sendAction(TargetApp.YOUTUBE, MediaAction.SkipForward)

        verify { transportControls.seekTo(22_000L) }
        verify(exactly = 0) { transportControls.skipToNext() }
    }

    @Test
    fun `skip backward uses previous when seek is unavailable but previous is supported`() {
        val playbackState = mockk<PlaybackState>()
        every { playbackState.actions } returns PlaybackState.ACTION_SKIP_TO_PREVIOUS
        every { controller.playbackState } returns playbackState
        every { controller.transportControls } returns transportControls
        every { mediaSessionMonitor.getController(TargetApp.YOUTUBE) } returns controller

        client.sendAction(TargetApp.YOUTUBE, MediaAction.SkipBackward)

        verify { transportControls.skipToPrevious() }
        verify(exactly = 0) { transportControls.seekTo(any()) }
    }
}
