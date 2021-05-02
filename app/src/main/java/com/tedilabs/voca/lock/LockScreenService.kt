package com.tedilabs.voca.lock

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.tedilabs.voca.R
import com.tedilabs.voca.view.ui.MainActivity
import timber.log.Timber

class LockScreenService : Service() {

    companion object {
        private const val CHANNEL_ID = "Voca"
        private const val NOTIFICATION_ID = 19960113

        fun start(context: Context) {
            val intent = Intent(context, LockScreenService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }

    private var lockScreenReceiver: BroadcastReceiver? = null

    override fun onCreate() {
        super.onCreate()
        Timber.d("-_-_- onCreate")

        registerLockScreenReceiver()
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("-_-_- onDestroy")

        unregisterLockScreenReceiver()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Timber.d("-_-_- onStartCommand")
        showForegroundNotification()

        return START_REDELIVER_INTENT
    }

    override fun onBind(p0: Intent?): IBinder? = null

    private fun registerLockScreenReceiver() {
        lockScreenReceiver?.let { return }

        lockScreenReceiver = LockScreenReceiver()
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
            priority = 100
        }

        registerReceiver(lockScreenReceiver, filter)
    }

    private fun unregisterLockScreenReceiver() {
        lockScreenReceiver?.let { unregisterReceiver(it) }
        lockScreenReceiver = null
    }

    private fun showForegroundNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationWithChannel()
        } else {
            createNotification()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationWithChannel() {
        val channelName = getString(R.string.channel_name)
        val resultIntent = MainActivity.intent(this)
        val stackBuilder = TaskStackBuilder.create(this).apply {
            addNextIntentWithParentStack(resultIntent)
        }
        val resultPendingIntent = stackBuilder
            .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

        NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_LOW)
            .also { channel ->
                channel.lightColor = ContextCompat.getColor(this, R.color.primary)
                (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                    .createNotificationChannel(channel)
            }

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
        val notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(ContextCompat.getColor(this, R.color.primary))
            .setContentTitle("Voca lock screen") // TODO: strings.xml
            .setContentText("Something Something") // TODO: strings.xml
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setContentIntent(resultPendingIntent)
            .build()
        NotificationManagerCompat.from(this)
            .notify(NOTIFICATION_ID, notificationBuilder.build())
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun createNotification() {
        val pendingIntent: PendingIntent = MainActivity.intent(this).let { intent ->
            PendingIntent.getActivity(this, 0, intent, 0)
        }
        val notification: Notification = NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.ic_notification) // TODO: real asset
            .setColor(ContextCompat.getColor(this, R.color.primary))
            .setContentTitle("Voca lock screen") // TODO: strings.xml
            .setContentText("Something Something") // TODO: strings.xml
            .setPriority(NotificationManager.IMPORTANCE_MIN) // TODO: test api 21
            .setFullScreenIntent(pendingIntent, true)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }
}
