package com.polaralias.audiofocus.ui.navigation

import com.polaralias.audiofocus.core.model.AppSettings
import javax.inject.Inject

enum class AppStartDestination {
    ONBOARDING,
    HOME
}

class LaunchDestinationResolver @Inject constructor() {
    fun resolve(
        settings: AppSettings,
        hasAllRequiredPermissions: Boolean
    ): AppStartDestination {
        if (!settings.hasCompletedOnboarding) {
            return AppStartDestination.ONBOARDING
        }

        return if (hasAllRequiredPermissions) {
            AppStartDestination.HOME
        } else {
            AppStartDestination.ONBOARDING
        }
    }
}
