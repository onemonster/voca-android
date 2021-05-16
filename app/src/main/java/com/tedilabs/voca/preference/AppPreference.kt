package com.tedilabs.voca.preference

import android.content.SharedPreferences
import androidx.core.content.edit
import com.tedilabs.voca.model.WordListKey
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreference @Inject constructor(
    private val sharedPreference: SharedPreferences
) {

    var lockScreenOn by prefBoolean(sharedPreference, PrefKey.LOCK_SCREEN_ON)

    var wordListKey: WordListKey
        get() = WordListKey(name = wordListName, version = wordListVersion)
        set(value) {
            wordListName = value.name
            wordListVersion = value.version
        }
    private var wordListName by prefString(sharedPreference, PrefKey.WORD_LIST_NAME)
    private var wordListVersion by prefString(sharedPreference, PrefKey.WORD_LIST_VERSION)

    fun clear(commit: Boolean = true) {
        sharedPreference.edit(commit = commit) { clear() }
    }
}
