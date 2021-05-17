package com.tedilabs.voca.view.ui

import androidx.lifecycle.ViewModel
import com.tedilabs.voca.model.Word
import com.tedilabs.voca.model.WordList
import com.tedilabs.voca.network.service.WordApiService
import com.tedilabs.voca.preference.AppPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject

@HiltViewModel
class WordViewModel @Inject constructor(
    private val wordApiService: WordApiService,
    private val appPreference: AppPreference
) : ViewModel() {

    private val wordSubject = BehaviorSubject.createDefault(Word.default)
    private val wordListSubject = BehaviorSubject.createDefault(WordList.default)
    private val wordListsSubject = BehaviorSubject.createDefault(emptyList<WordList>())

    fun observeWord(): Observable<Word> = wordSubject.hide()
    fun observeWordList(): Observable<WordList> = wordListSubject.hide()
    fun observeWordLists(): Observable<List<WordList>> = wordListsSubject.hide()

    val word: Word = wordSubject.value

    fun showNextWord() {
        // TODO wordSubject.onNext(nextWord)
    }

    fun showPrevWord() {
        // TODO wordSubject.onNext(prevWord)
    }

    fun setWordList(wordList: WordList) {
        wordListSubject.onNext(wordList)
        appPreference.wordListKey = wordList.key
    }

    fun getWordList(): Completable {
        return wordApiService.getWordLists()
            .doOnSuccess { wordLists ->
                wordListsSubject.onNext(wordLists)
                if (wordListSubject.value == WordList.default) {
                    wordLists.find {
                        it.key.name == appPreference.wordListKey.name
                    }?.let {
                        if (it.key.version == appPreference.wordListKey.version) {
                            setWordList(it)
                        } else {
                            // TODO: download new database
                            setWordList(it)
                        }
                    } ?: wordLists.firstOrNull()?.let {
                        // TODO: download new database
                        setWordList(it)
                    }
                }
            }
            .ignoreElement()
            .subscribeOn(Schedulers.io())
    }
}
