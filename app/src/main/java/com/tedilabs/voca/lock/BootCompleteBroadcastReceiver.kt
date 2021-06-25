package com.tedilabs.voca.lock

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import timber.log.Timber

class BootCompleteBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                Timber.d("-_-_- onReceive")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(Intent(context, RestartService::class.java))
                } else {
                    context.startService(Intent(context, RestartService::class.java))
                }
            }
        }
    }
}
