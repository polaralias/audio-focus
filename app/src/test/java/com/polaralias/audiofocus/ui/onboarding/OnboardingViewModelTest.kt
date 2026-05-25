package com.polaralias.audiofocus.ui.onboarding

import com.polaralias.audiofocus.core.model.AppSettings
import com.polaralias.audiofocus.core.model.OverlaySettings
import com.polaralias.audiofocus.core.model.ThemeConfig
import com.polaralias.audiofocus.domain.PermissionManager
import com.polaralias.audiofocus.domain.settings.SettingsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `complete onboarding persists completion before invoking callback`() = runTest {
        val settingsRepository = FakeSettingsRepository()
        val permissionManager = mockk<PermissionManager>(relaxed = true)
        val viewModel = OnboardingViewModel(permissionManager, settingsRepository)
        var callbackInvoked = false

        viewModel.completeOnboarding {
            callbackInvoked = true
        }

        advanceUntilIdle()

        assertTrue(settingsRepository.lastOnboardingCompletedValue)
        assertTrue(callbackInvoked)
    }

    private class FakeSettingsRepository : SettingsRepository {
        var lastOnboardingCompletedValue = false

        override val overlaySettings: Flow<OverlaySettings> = flowOf(OverlaySettings())
        override val appSettings: Flow<AppSettings> = flowOf(AppSettings())

        override suspend fun setMonitoringEnabled(enabled: Boolean) = Unit

        override suspend fun setOnboardingCompleted(completed: Boolean) {
            lastOnboardingCompletedValue = completed
        }

        override suspend fun setBlurEnabled(enabled: Boolean) = Unit

        override suspend fun setBackgroundColor(color: Long) = Unit

        override suspend fun setYoutubeTheme(theme: ThemeConfig) = Unit

        override suspend fun setYoutubeMusicTheme(theme: ThemeConfig) = Unit
    }
}
