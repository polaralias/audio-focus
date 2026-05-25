package com.polaralias.audiofocus.service.monitor

import android.app.Notification
import android.service.notification.StatusBarNotification
import com.polaralias.audiofocus.core.model.TargetApp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationMonitor @Inject constructor() {
    private val _activeMediaNotifications = MutableStateFlow<Set<TargetApp>>(emptySet())
    val activeMediaNotifications = _activeMediaNotifications.asStateFlow()
    private val activeNotificationKeysByApp = mutableMapOf<TargetApp, MutableSet<String>>()

    fun onNotificationPosted(sbn: StatusBarNotification) {
        val packageName = sbn.packageName
        val targetApp = TargetApp.entries.find { it.packageName == packageName } ?: return

        if (isMediaNotification(sbn)) {
            recordActiveMediaNotification(targetApp, sbn.key)
        }
    }

    fun onNotificationRemoved(sbn: StatusBarNotification) {
        val packageName = sbn.packageName
        val targetApp = TargetApp.entries.find { it.packageName == packageName } ?: return

        if (isMediaNotification(sbn)) {
            clearActiveMediaNotification(targetApp, sbn.key)
        }
    }

    internal fun recordActiveMediaNotification(targetApp: TargetApp, key: String) {
        val activeKeys = activeNotificationKeysByApp.getOrPut(targetApp) { mutableSetOf() }
        activeKeys += key
        _activeMediaNotifications.update { it + targetApp }
    }

    internal fun clearActiveMediaNotification(targetApp: TargetApp, key: String) {
        val activeKeys = activeNotificationKeysByApp[targetApp] ?: return
        activeKeys -= key
        if (activeKeys.isEmpty()) {
            activeNotificationKeysByApp.remove(targetApp)
            _activeMediaNotifications.update { it - targetApp }
        }
    }

    private fun isMediaNotification(sbn: StatusBarNotification): Boolean {
        val extras = sbn.notification.extras
        val template = extras.getString(Notification.EXTRA_TEMPLATE)
        return template == "android.app.Notification\$MediaStyle" ||
               template == "android.media.style.MediaStyle"
    }
}
