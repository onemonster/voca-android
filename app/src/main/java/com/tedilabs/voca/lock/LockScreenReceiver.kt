package com.tedilabs.voca.lock

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.tedilabs.voca.view.ui.MainActivity
import timber.log.Timber

class LockScreenReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Timber.d("-_-_- Broadcast Received.")

        val lockScreenIntent = MainActivity.lockScreenIntent(context)
        when (intent.action) {
            Intent.ACTION_SCREEN_OFF -> {
                Timber.d("-_-_- Screen is turned off")
            }
            Intent.ACTION_SCREEN_ON -> {
                Timber.d("-_-_- Screen is turned on")
                context.startActivity(lockScreenIntent)
            }
        }
    }
}
