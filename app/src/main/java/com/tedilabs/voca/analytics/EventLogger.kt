package com.tedilabs.voca.analytics

import android.app.Application
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.tedilabs.voca.model.Word
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventLogger @Inject constructor(
    application: Application
) {

    private val firebaseAnalytics = FirebaseAnalytics.getInstance(application)

    fun logClickNextWord(word: Word, isLockScreen: Boolean) {
        logEvent("click_next_word", clickWordBundle(word, isLockScreen))
    }

    fun logClickPrevWord(word: Word, isLockScreen: Boolean) {
        logEvent("click_prev_word", clickWordBundle(word, isLockScreen))
    }

    private fun logEvent(name: String, params: Bundle) {
        val paramsString = params.keySet().joinToString(",") { key ->
            "$key=${params.get(key)}"
        }
        Timber.d("LogEvent $name, params: $paramsString")
        firebaseAnalytics.logEvent(name, params)
    }

    private fun clickWordBundle(word: Word, isLockScreen: Boolean) = Bundle().apply {
        putString("word", "${word.id} ${word.word}")
        putBoolean("is_lock_screen", isLockScreen)
    }
}
