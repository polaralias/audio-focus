package com.polaralias.audiofocus.core.logic

import android.media.session.PlaybackState
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MediaControlCapabilitiesResolverTest {

    private val resolver = MediaControlCapabilitiesResolver()

    @Test
    fun `seek capability requires seek action`() {
        val capabilities = resolver.resolve(PlaybackState.ACTION_PLAY or PlaybackState.ACTION_PAUSE)

        assertFalse(capabilities.canSeek)
    }

    @Test
    fun `seek capability is enabled when session supports seek`() {
        val capabilities = resolver.resolve(PlaybackState.ACTION_SEEK_TO)

        assertTrue(capabilities.canSeek)
    }

    @Test
    fun `skip forward and backward are enabled from supported transport actions`() {
        val capabilities = resolver.resolve(
            PlaybackState.ACTION_SKIP_TO_NEXT or
                PlaybackState.ACTION_SKIP_TO_PREVIOUS
        )

        assertTrue(capabilities.canSkipForward)
        assertTrue(capabilities.canSkipBackward)
    }

    @Test
    fun `play and pause are gated independently`() {
        val capabilities = resolver.resolve(PlaybackState.ACTION_PLAY)

        assertTrue(capabilities.canPlay)
        assertFalse(capabilities.canPause)
    }
}
