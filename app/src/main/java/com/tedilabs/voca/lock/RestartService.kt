package com.tedilabs.voca.lock

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.content.ContextCompat
import com.tedilabs.voca.R
import com.tedilabs.voca.view.ui.MainActivity

class RestartService : Service() {

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "Voca"
        private const val NOTIFICATION_ID = 19960113
    }

    override fun onBind(p0: Intent?): IBinder? = null

    private fun createNotification(): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                getString(R.string.channel_name),
                NotificationManager.IMPORTANCE_LOW
            )
                .also { channel ->
                    channel.lightColor = ContextCompat.getColor(this, R.color.primary)
                    channel.setShowBadge(false)
                }
            notificationManager.createNotificationChannel(channel)
        }

        val pendingIntent: PendingIntent = MainActivity.intent(this).let { intent ->
            PendingIntent.getActivity(this, 0, intent, 0)
        }

        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
        } else {
            Notification.Builder(this)
        }

        return builder
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(ContextCompat.getColor(this, R.color.primary))
            .setContentTitle(getString(R.string.foreground_notification_title))
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setContentIntent(pendingIntent)
            .build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createNotification()

        startForeground(NOTIFICATION_ID, notification)

        startService(Intent(this, LockScreenService::class.java))

        stopForeground(true)
        stopSelf()

        return START_NOT_STICKY
    }
}
