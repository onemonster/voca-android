package com.tedilabs.voca.lock

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.content.ContextCompat
import com.tedilabs.voca.R
import com.tedilabs.voca.view.ui.MainActivity

private const val NOTIFICATION_CHANNEL_ID = "Voca"
private const val NOTIFICATION_CHANNEL_NAME = "voca"
const val NOTIFICATION_ID = 19960113

fun createNotification(context: Context): Notification {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
            .also { channel ->
                channel.lightColor = ContextCompat.getColor(context, R.color.primary)
                channel.setShowBadge(false)
            }
        notificationManager.createNotificationChannel(channel)
    }

    val pendingIntent: PendingIntent = MainActivity.intent(context).let { intent ->
        PendingIntent.getActivity(context, 0, intent, 0)
    }

    val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Notification.Builder(context, NOTIFICATION_CHANNEL_ID)
    } else {
        Notification.Builder(context)
    }

    return builder
        .setOngoing(true)
        .setSmallIcon(R.drawable.ic_notification)
        .setColor(ContextCompat.getColor(context, R.color.primary))
        .setContentTitle(context.getString(R.string.foreground_notification_title))
        .setPriority(NotificationManager.IMPORTANCE_MIN)
        .setCategory(Notification.CATEGORY_SERVICE)
        .setContentIntent(pendingIntent)
        .build()
}
