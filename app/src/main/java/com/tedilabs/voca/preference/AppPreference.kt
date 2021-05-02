package com.tedilabs.voca.preference

import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreference @Inject constructor(
    private val sharedPreference: SharedPreferences
) {

    var lockScreenOn by prefBoolean(sharedPreference, PrefKey.LOCK_SCREEN_ON)

    fun clear(commit: Boolean = true) {
        sharedPreference.edit(commit = commit) { clear() }
    }
}
