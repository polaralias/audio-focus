package com.polaralias.audiofocus.ui.navigation

import com.polaralias.audiofocus.core.model.AppSettings
import org.junit.Assert.assertEquals
import org.junit.Test

class LaunchDestinationResolverTest {

    private val resolver = LaunchDestinationResolver()

    @Test
    fun `fresh install starts at onboarding`() {
        val destination = resolver.resolve(
            settings = AppSettings(hasCompletedOnboarding = false),
            hasAllRequiredPermissions = false
        )

        assertEquals(AppStartDestination.ONBOARDING, destination)
    }

    @Test
    fun `completed onboarding with all permissions starts at home`() {
        val destination = resolver.resolve(
            settings = AppSettings(hasCompletedOnboarding = true),
            hasAllRequiredPermissions = true
        )

        assertEquals(AppStartDestination.HOME, destination)
    }

    @Test
    fun `completed onboarding falls back to onboarding when permissions are missing`() {
        val destination = resolver.resolve(
            settings = AppSettings(hasCompletedOnboarding = true),
            hasAllRequiredPermissions = false
        )

        assertEquals(AppStartDestination.ONBOARDING, destination)
    }
}
