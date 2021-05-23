package com.tedilabs.voca.preference

import android.content.SharedPreferences
import androidx.core.content.edit
import com.tedilabs.voca.model.WordList
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreference @Inject constructor(
    private val sharedPreference: SharedPreferences
) {

    var lockScreenOn by prefBoolean(sharedPreference, PrefKey.LOCK_SCREEN_ON)
    var wordListName by prefString(sharedPreference, PrefKey.WORD_LIST_NAME)

    fun getCursor(wordList: WordList): Int =
        sharedPreference.getInt(wordList.key, 0)

    fun setCursor(wordList: WordList, cursor: Int) =
        sharedPreference.edit {
            putInt(wordList.key, cursor)
        }

    fun clear(commit: Boolean = true) {
        sharedPreference.edit(commit = commit) { clear() }
    }
}
