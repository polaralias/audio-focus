package com.polaralias.audiofocus.service.monitor

import com.polaralias.audiofocus.core.model.TargetApp
import org.junit.Assert.assertEquals
import org.junit.Test

class NotificationMonitorTest {

    private val monitor = NotificationMonitor()

    @Test
    fun `posting a media notification marks the app active`() {
        monitor.recordActiveMediaNotification(TargetApp.YOUTUBE, "youtube:1")

        assertEquals(setOf(TargetApp.YOUTUBE), monitor.activeMediaNotifications.value)
    }

    @Test
    fun `removing one of multiple media notifications keeps the app active`() {
        monitor.recordActiveMediaNotification(TargetApp.YOUTUBE, "youtube:1")
        monitor.recordActiveMediaNotification(TargetApp.YOUTUBE, "youtube:2")
        monitor.clearActiveMediaNotification(TargetApp.YOUTUBE, "youtube:1")

        assertEquals(setOf(TargetApp.YOUTUBE), monitor.activeMediaNotifications.value)
    }

    @Test
    fun `removing the last media notification clears the app`() {
        monitor.recordActiveMediaNotification(TargetApp.YOUTUBE, "youtube:1")
        monitor.clearActiveMediaNotification(TargetApp.YOUTUBE, "youtube:1")

        assertEquals(emptySet<TargetApp>(), monitor.activeMediaNotifications.value)
    }
}
