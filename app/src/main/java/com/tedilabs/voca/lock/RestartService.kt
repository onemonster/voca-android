package com.tedilabs.voca.lock

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import com.tedilabs.voca.preference.AppPreference
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class RestartService : Service() {

    @Inject
    lateinit var appPreference: AppPreference

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("-_-_- onStartCommand ${appPreference.lockScreenOn}")

        if (appPreference.lockScreenOn) {
            handleLockScreen()
        } else {
            stopSelf()
        }

        return START_NOT_STICKY
    }

    private fun handleLockScreen() {
        val notification = createNotification(this)
        startForeground(NOTIFICATION_ID, notification)

        startLockScreenService()

        stopForeground(true)
        stopSelf()
    }

    private fun startLockScreenService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(this, LockScreenService::class.java))
        } else {
            startService(Intent(this, LockScreenService::class.java))
        }
    }
}
