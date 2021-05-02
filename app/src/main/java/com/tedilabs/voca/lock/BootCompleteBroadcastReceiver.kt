package com.tedilabs.voca.lock

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.tedilabs.voca.preference.AppPreference
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BootCompleteBroadcastReceiver : BroadcastReceiver() {

    @Inject
    lateinit var appPreference: AppPreference

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                if (appPreference.lockScreenOn) {
                    LockScreenService.start(context)
                }
            }
        }
    }
}
