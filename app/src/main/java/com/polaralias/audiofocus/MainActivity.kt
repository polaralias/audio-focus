package com.polaralias.audiofocus

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.polaralias.audiofocus.domain.PermissionManager
import com.polaralias.audiofocus.domain.settings.SettingsRepository
import com.polaralias.audiofocus.service.AudioFocusService
import com.polaralias.audiofocus.ui.navigation.AppStartDestination
import com.polaralias.audiofocus.ui.navigation.AppNavigation
import com.polaralias.audiofocus.ui.navigation.LaunchDestinationResolver
import com.polaralias.audiofocus.ui.navigation.Routes
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    @Inject
    lateinit var permissionManager: PermissionManager

    @Inject
    lateinit var launchDestinationResolver: LaunchDestinationResolver

    private var startDestination by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            val settings = settingsRepository.appSettings.first()
            startDestination = when (
                launchDestinationResolver.resolve(
                    settings = settings,
                    hasAllRequiredPermissions = permissionManager.hasAllRequiredPermissions()
                )
            ) {
                AppStartDestination.ONBOARDING -> Routes.ONBOARDING
                AppStartDestination.HOME -> Routes.HOME
            }

            if (settings.isMonitoringEnabled) {
                val intent = Intent(this@MainActivity, AudioFocusService::class.java)
                startForegroundService(intent)
            }
        }

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val initialRoute = startDestination
                    if (initialRoute == null) {
                        Text("Loading...")
                    } else {
                        AppNavigation(startDestination = initialRoute)
                    }
                }
            }
        }
    }
}
