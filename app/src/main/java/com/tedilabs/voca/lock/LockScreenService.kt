package com.tedilabs.voca.lock

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import androidx.core.content.ContextCompat
import com.tedilabs.voca.R
import com.tedilabs.voca.view.ui.MainActivity
import timber.log.Timber

class LockScreenService : Service() {

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "Voca"
        private const val NOTIFICATION_ID = 19960113

        fun start(context: Context) {
            val intent = Intent(context, LockScreenService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, LockScreenService::class.java)
            context.stopService(intent)
        }
    }

    private var lockScreenReceiver: BroadcastReceiver? = null

    override fun onCreate() {
        super.onCreate()
        Timber.d("-_-_- onCreate")

        showForegroundNotification()
        registerLockScreenReceiver()
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("-_-_- onDestroy")

        unregisterLockScreenReceiver()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("-_-_- onStartCommand intent: $intent flags: $flags startId: $startId")

        return START_STICKY
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
        startForeground(NOTIFICATION_ID, createNotification())
    }

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
}
