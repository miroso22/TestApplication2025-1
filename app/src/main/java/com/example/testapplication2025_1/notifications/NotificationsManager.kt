package com.example.testapplication2025_1.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.testapplication2025_1.hasPermission

class NotificationsManager(private val context: Context) {
    private val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    fun createNotification(notification: Notification) {
        if (context.hasPermission(Manifest.permission.POST_NOTIFICATIONS))
            manager.notify(NOTIFICATION_ID, notification.getBuilder(context).build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(CHANNEL_ID, "Test", NotificationManager.IMPORTANCE_DEFAULT)
        manager.createNotificationChannel(channel)
    }

    companion object {
        const val CHANNEL_ID = "default"
        const val MESSAGE_KEY = "message"
        const val NOTIFICATION_ID = 1
    }
}