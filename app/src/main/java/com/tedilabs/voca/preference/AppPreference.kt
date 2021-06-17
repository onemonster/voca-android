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

    private var wordListName by prefString(sharedPreference, PrefKey.WORD_LIST_NAME)
    private var wordListSourceLanguage by prefString(
        sharedPreference,
        PrefKey.WORD_LIST_SOURCE_LANGUAGE
    )
    private var wordListTargetLanguage by prefString(
        sharedPreference,
        PrefKey.WORD_LIST_TARGET_LANGUAGE
    )
    private var wordListVersion by prefString(sharedPreference, PrefKey.WORD_LIST_VERSION)
    private var wordListUrl by prefString(sharedPreference, PrefKey.WORD_LIST_URL)

    var wordList: WordList?
        get() = WordList(
            name = wordListName,
            sourceLanguage = wordListSourceLanguage,
            targetLanguage = wordListTargetLanguage,
            version = wordListVersion,
            url = wordListUrl,
        ).takeIf { wordListName.isNotEmpty() }
        set(value) {
            wordListName = value?.name ?: ""
            wordListSourceLanguage = value?.sourceLanguage ?: ""
            wordListTargetLanguage = value?.targetLanguage ?: ""
            wordListVersion = value?.version ?: ""
            wordListUrl = value?.url ?: ""
        }

    fun getCursor(wordList: WordList): Int =
        sharedPreference.getInt(wordList.key, 1)

    fun setCursor(wordList: WordList, cursor: Int) =
        sharedPreference.edit {
            putInt(wordList.key, cursor)
        }

    fun clear(commit: Boolean = true) {
        sharedPreference.edit(commit = commit) { clear() }
    }
}
