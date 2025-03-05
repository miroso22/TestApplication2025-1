package com.example.testapplication2025_1.notifications

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import com.example.testapplication2025_1.MainActivity
import com.example.testapplication2025_1.R

enum class Notification {
    Basic,
    BigText,
    Inbox,
    BigPicture,
    Messaging,
    Interactable,
}

@SuppressLint("NewApi")
fun Notification.getBuilder(context: Context) = when (this) {
    Notification.Basic -> NotificationCompat.Builder(context, NotificationsManager.CHANNEL_ID)
        .setSmallIcon(android.R.drawable.ic_menu_add)
        .setContentTitle("Basic Notification")
        .setContentText("Lorem ipsum dolor sit amet")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    Notification.BigText -> NotificationCompat.Builder(context, NotificationsManager.CHANNEL_ID)
        .setSmallIcon(android.R.drawable.ic_menu_add)
        .setContentTitle("Big Text Notification")
        .setContentText("Lorem ipsum dolor sit amet")
        .setStyle(
            NotificationCompat.BigTextStyle()
                .setSummaryText("Lorem ipsum dolor sit amet")
                .bigText("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam leo tellus, lobortis vel erat eu, viverra iaculis metus. Morbi sodales ipsum vel arcu commodo lacinia. Etiam sit amet arcu lobortis, elementum augue non, dignissim ante")
        )
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    Notification.Inbox -> NotificationCompat.Builder(context, NotificationsManager.CHANNEL_ID)
        .setSmallIcon(android.R.drawable.ic_menu_add)
        .setContentTitle("Inbox Notification")
        .setContentText("Lorem ipsum dolor sit amet")
        .setStyle(
            NotificationCompat.InboxStyle()
                .addLine("Line 1")
                .addLine("Line 2")
                .addLine("Line 3")
        )
    Notification.BigPicture -> NotificationCompat.Builder(context, NotificationsManager.CHANNEL_ID)
        .setSmallIcon(android.R.drawable.ic_menu_add)
        .setContentTitle("Big Image Notification")
        .setContentText("Lorem ipsum dolor sit amet")
        .setStyle(
            NotificationCompat.BigPictureStyle()
                .bigPicture(Icon.createWithResource(context, R.drawable.ic_launcher_background))
        )
    Notification.Messaging -> NotificationCompat.Builder(context, NotificationsManager.CHANNEL_ID)
        .setSmallIcon(android.R.drawable.ic_menu_add)
        .setStyle(
            NotificationCompat.MessagingStyle(me)
                .addMessage(NotificationCompat.MessagingStyle.Message("Message1", 0, me))
                .addMessage(NotificationCompat.MessagingStyle.Message("Message2", 1, you))
                .addMessage(NotificationCompat.MessagingStyle.Message("Message3", 2, me))
                .addMessage(NotificationCompat.MessagingStyle.Message("Message4", 3, you))
        )
    Notification.Interactable -> NotificationCompat.Builder(context, NotificationsManager.CHANNEL_ID)
        .setSmallIcon(android.R.drawable.ic_menu_add)
        .addAction(android.R.drawable.ic_menu_manage, "Action1", createPendingIntent(context, "Tap on Action1", 0))
        .addAction(android.R.drawable.ic_menu_manage, "Action2", createPendingIntent(context, "Tap on Action2", 1))
}

private val me = Person.Builder()
    .setName("Me")
    .setImportant(true)
    .build()

private val you = Person.Builder()
    .setName("You")
    .setImportant(true)
    .build()

private fun createPendingIntent(context: Context, message: String, code: Int): PendingIntent {
    val intent = Intent(context, MainActivity::class.java)
        .putExtra(NotificationsManager.MESSAGE_KEY, message)
        .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
    val flags = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    return PendingIntent.getActivity(context, code, intent, flags)
}