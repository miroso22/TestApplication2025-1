package com.example.testapplication2025_1.notifications

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.testapplication2025_1.requestPermissions

@Composable
fun NotificationsScreen(modifier: Modifier = Modifier, onBack: () -> Unit) {
    requestPermissions(listOf(Manifest.permission.POST_NOTIFICATIONS))

    val context = LocalContext.current
    val notificationsManager = remember { NotificationsManager(context) }

    BackHandler(onBack = onBack)

    Column(modifier = modifier) {
        Notification.entries.forEach { type ->
            Button(onClick = { notificationsManager.createNotification(type) }) {
                Text(type.name)
            }
        }
    }
}