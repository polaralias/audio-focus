package com.polaralias.audiofocus.core.logic

import com.polaralias.audiofocus.core.model.*
import com.polaralias.audiofocus.service.monitor.AccessibilityMonitor
import com.polaralias.audiofocus.service.monitor.AccessibilityState
import com.polaralias.audiofocus.service.monitor.ForegroundAppDetector
import com.polaralias.audiofocus.service.monitor.MediaSessionMonitor
import com.polaralias.audiofocus.service.monitor.NotificationMonitor
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class PlaybackStateEngineTest {

    private lateinit var accessibilityMonitor: AccessibilityMonitor
    private lateinit var mediaSessionMonitor: MediaSessionMonitor
    private lateinit var notificationMonitor: NotificationMonitor
    private lateinit var foregroundAppDetector: ForegroundAppDetector
    private lateinit var engine: PlaybackStateEngine

    private val accessibilityStates = MutableStateFlow<Map<TargetApp, AccessibilityState>>(emptyMap())
    private val mediaSessionStates = MutableStateFlow<Map<TargetApp, PlaybackStateSimplified>>(emptyMap())
    private val activeNotifications = MutableStateFlow<Set<TargetApp>>(emptySet())
    private val foregroundPackage = MutableStateFlow<String?>(null)

    @Before
    fun setup() {
        mockkStatic(android.util.Log::class)
        every { android.util.Log.d(any(), any()) } returns 0
        every { android.util.Log.e(any(), any()) } returns 0

        accessibilityMonitor = mockk()
        mediaSessionMonitor = mockk()
        notificationMonitor = mockk()
        foregroundAppDetector = mockk()

        every { accessibilityMonitor.states } returns accessibilityStates
        every { mediaSessionMonitor.observe() } returns mediaSessionStates
        every { notificationMonitor.activeMediaNotifications } returns activeNotifications
        every { foregroundAppDetector.foregroundPackage } returns foregroundPackage

        engine = PlaybackStateEngine(accessibilityMonitor, mediaSessionMonitor, notificationMonitor, foregroundAppDetector)
    }

    @Test
    fun `YouTube Fullscreen Video Playing should show overlay`() = runTest {
        // Arrange
        accessibilityStates.value = mapOf(
            TargetApp.YOUTUBE to AccessibilityState(WindowState.FOREGROUND_FULLSCREEN, PlaybackType.VISIBLE_VIDEO)
        )
        mediaSessionStates.value = mapOf(TargetApp.YOUTUBE to PlaybackStateSimplified.PLAYING)
        activeNotifications.value = setOf(TargetApp.YOUTUBE)

        // Act
        val decision = engine.overlayDecision.first()

        // Assert
        assertEquals(true, decision.shouldOverlay)
        assertEquals(OverlayMode.FULL_SCREEN, decision.overlayMode)
        assertEquals(TargetApp.YOUTUBE, decision.targetApp)
    }

    @Test
    fun `YouTube Paused should hide overlay`() = runTest {
        // Arrange
        accessibilityStates.value = mapOf(
            TargetApp.YOUTUBE to AccessibilityState(WindowState.FOREGROUND_FULLSCREEN, PlaybackType.VISIBLE_VIDEO)
        )
        mediaSessionStates.value = mapOf(TargetApp.YOUTUBE to PlaybackStateSimplified.PAUSED)
        activeNotifications.value = setOf(TargetApp.YOUTUBE)

        // Act
        val decision = engine.overlayDecision.first()

        // Assert
        assertEquals(false, decision.shouldOverlay)
    }

    @Test
    fun `YouTube Audio Only should hide overlay`() = runTest {
        // Arrange
        accessibilityStates.value = mapOf(
            TargetApp.YOUTUBE to AccessibilityState(WindowState.FOREGROUND_FULLSCREEN, PlaybackType.NONE) // No video surface
        )
        mediaSessionStates.value = mapOf(TargetApp.YOUTUBE to PlaybackStateSimplified.PLAYING)
        activeNotifications.value = setOf(TargetApp.YOUTUBE)

        // Act
        val decision = engine.overlayDecision.first()

        // Assert
        assertEquals(false, decision.shouldOverlay)
    }

    @Test
    fun `YouTube Music Video Playing should show overlay`() = runTest {
        // Arrange
        accessibilityStates.value = mapOf(
            TargetApp.YOUTUBE_MUSIC to AccessibilityState(WindowState.FOREGROUND_FULLSCREEN, PlaybackType.VISIBLE_VIDEO)
        )
        mediaSessionStates.value = mapOf(TargetApp.YOUTUBE_MUSIC to PlaybackStateSimplified.PLAYING)
        activeNotifications.value = setOf(TargetApp.YOUTUBE_MUSIC)

        // Act
        val decision = engine.overlayDecision.first()

        // Assert
        assertEquals(true, decision.shouldOverlay)
        assertEquals(OverlayMode.FULL_SCREEN, decision.overlayMode)
        assertEquals(TargetApp.YOUTUBE_MUSIC, decision.targetApp)
    }

    @Test
    fun `YouTube Music Audio Playing should hide overlay`() = runTest {
        // Arrange
        accessibilityStates.value = mapOf(
            TargetApp.YOUTUBE_MUSIC to AccessibilityState(WindowState.FOREGROUND_FULLSCREEN, PlaybackType.NONE)
        )
        mediaSessionStates.value = mapOf(TargetApp.YOUTUBE_MUSIC to PlaybackStateSimplified.PLAYING)
        activeNotifications.value = setOf(TargetApp.YOUTUBE_MUSIC)

        // Act
        val decision = engine.overlayDecision.first()

        // Assert
        assertEquals(false, decision.shouldOverlay)
    }

    @Test
    fun `YouTube Fullscreen but another app in foreground should hide overlay`() = runTest {
        // Arrange
        accessibilityStates.value = mapOf(
            TargetApp.YOUTUBE to AccessibilityState(WindowState.FOREGROUND_FULLSCREEN, PlaybackType.VISIBLE_VIDEO)
        )
        mediaSessionStates.value = mapOf(TargetApp.YOUTUBE to PlaybackStateSimplified.PLAYING)
        activeNotifications.value = setOf(TargetApp.YOUTUBE)
        foregroundPackage.value = "com.other.app"

        // Act
        val decision = engine.overlayDecision.first()

        // Assert
        assertEquals(false, decision.shouldOverlay)
    }

    @Test
    fun `YouTube visible playing without active media notification should hide overlay`() = runTest {
        accessibilityStates.value = mapOf(
            TargetApp.YOUTUBE to AccessibilityState(WindowState.FOREGROUND_FULLSCREEN, PlaybackType.VISIBLE_VIDEO)
        )
        mediaSessionStates.value = mapOf(TargetApp.YOUTUBE to PlaybackStateSimplified.PLAYING)
        activeNotifications.value = emptySet()

        val decision = engine.overlayDecision.first()

        assertEquals(false, decision.shouldOverlay)
    }

    @Test
    fun `YouTube visible playing without active media notification but confirmed foreground should show overlay`() = runTest {
        accessibilityStates.value = mapOf(
            TargetApp.YOUTUBE to AccessibilityState(WindowState.FOREGROUND_FULLSCREEN, PlaybackType.VISIBLE_VIDEO)
        )
        mediaSessionStates.value = mapOf(TargetApp.YOUTUBE to PlaybackStateSimplified.PLAYING)
        activeNotifications.value = emptySet()
        foregroundPackage.value = TargetApp.YOUTUBE.packageName

        val decision = engine.overlayDecision.first()

        assertEquals(true, decision.shouldOverlay)
        assertEquals(OverlayMode.FULL_SCREEN, decision.overlayMode)
        assertEquals(TargetApp.YOUTUBE, decision.targetApp)
    }

    @Test
    fun `YouTube Music visible playing without active media notification should hide overlay`() = runTest {
        accessibilityStates.value = mapOf(
            TargetApp.YOUTUBE_MUSIC to AccessibilityState(WindowState.FOREGROUND_FULLSCREEN, PlaybackType.VISIBLE_VIDEO)
        )
        mediaSessionStates.value = mapOf(TargetApp.YOUTUBE_MUSIC to PlaybackStateSimplified.PLAYING)
        activeNotifications.value = emptySet()

        val decision = engine.overlayDecision.first()

        assertEquals(false, decision.shouldOverlay)
    }

    @Test
    fun `YouTube minimised visible video with active notification should show overlay`() = runTest {
        accessibilityStates.value = mapOf(
            TargetApp.YOUTUBE to AccessibilityState(WindowState.FOREGROUND_MINIMISED, PlaybackType.VISIBLE_VIDEO)
        )
        mediaSessionStates.value = mapOf(TargetApp.YOUTUBE to PlaybackStateSimplified.PLAYING)
        activeNotifications.value = setOf(TargetApp.YOUTUBE)

        val decision = engine.overlayDecision.first()

        assertEquals(true, decision.shouldOverlay)
        assertEquals(TargetApp.YOUTUBE, decision.targetApp)
    }

    @Test
    fun `YouTube audio only with active notification should hide overlay`() = runTest {
        accessibilityStates.value = mapOf(
            TargetApp.YOUTUBE to AccessibilityState(WindowState.FOREGROUND_MINIMISED, PlaybackType.AUDIO_ONLY)
        )
        mediaSessionStates.value = mapOf(TargetApp.YOUTUBE to PlaybackStateSimplified.PLAYING)
        activeNotifications.value = setOf(TargetApp.YOUTUBE)

        val decision = engine.overlayDecision.first()

        assertEquals(false, decision.shouldOverlay)
    }
}
