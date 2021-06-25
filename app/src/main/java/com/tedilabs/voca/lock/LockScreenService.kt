package com.tedilabs.voca.lock

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import com.tedilabs.voca.preference.AppPreference
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class LockScreenService : Service() {

    @Inject
    lateinit var appPreference: AppPreference

    private var lockScreenReceiver: BroadcastReceiver? = null

    override fun onCreate() {
        super.onCreate()
        Timber.d("-_-_- onCreate")

        val notification = createNotification(this)
        startForeground(NOTIFICATION_ID, notification)
        registerLockScreenReceiver()
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("-_-_- onDestroy ${appPreference.lockScreenOn}")

        unregisterLockScreenReceiver()
        if (appPreference.lockScreenOn) {
            setAlarmTimer()
        }
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

    private fun setAlarmTimer() {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            add(Calendar.SECOND, 5)
        }
        val intent = Intent(this, AlarmReceiver::class.java)
        val sender = PendingIntent.getBroadcast(this, 0, intent, 0)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, sender)
    }
}
