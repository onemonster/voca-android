package com.tedilabs.voca.view.ui

import androidx.lifecycle.ViewModel
import com.tedilabs.voca.model.Word
import com.tedilabs.voca.model.WordList
import com.tedilabs.voca.network.service.WordApiService
import com.tedilabs.voca.preference.AppPreference
import com.tedilabs.voca.repository.AppRepositoryManager
import com.tedilabs.voca.repository.WordRepository
import com.tedilabs.voca.util.Optional
import com.tedilabs.voca.util.unwrapOptional
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class WordViewModel @Inject constructor(
    private val wordApiService: WordApiService,
    private val appPreference: AppPreference,
    private val appRepositoryManager: AppRepositoryManager,
) : ViewModel() {

    private val wordSubject = BehaviorSubject.createDefault(Word.default)
    private val wordListSubject = BehaviorSubject.createDefault(Optional.empty<WordList>())
    private val wordListsSubject = BehaviorSubject.createDefault(emptyList<WordList>())
    private val wordCountSubject = BehaviorSubject.createDefault(0)

    fun observeWord(): Observable<Word> = wordSubject.hide()
    fun observeWordList(): Observable<Optional<WordList>> = wordListSubject.hide()
    fun observeWordLists(): Observable<List<WordList>> = wordListsSubject.hide()
    fun observeWordCount(): Observable<Int> = wordCountSubject.hide()

    val word: Word get() = wordSubject.value

    private fun getWordRepository(): Single<WordRepository> {
        return wordListSubject
            .unwrapOptional()
            .firstOrError()
            .flatMap {
                appRepositoryManager.getWordRepository(it)
            }
    }

    fun showNextWord(): Completable {
        return getWordRepository()
            .flatMap { it.getNext() }
            .doOnSuccess { word ->
                wordSubject.onNext(word)
            }
            .ignoreElement()
    }

    fun showPrevWord(): Completable {
        return getWordRepository()
            .flatMap { it.getPrev() }
            .doOnSuccess { word ->
                wordSubject.onNext(word)
            }
            .ignoreElement()
    }

    fun setWordList(wordList: WordList): Completable {
        return appRepositoryManager.getWordRepository(wordList)
            .flatMap {
                wordCountSubject.onNext(it.wordCount)
                it.getCurrent()
            }
            .doOnSuccess {
                wordSubject.onNext(it)
                wordListSubject.onNext(Optional.from(wordList))
                appPreference.wordListName = wordList.name
            }
            .ignoreElement()
    }

    fun initialize(): Completable {
        return wordApiService.getWordLists()
            .flatMapCompletable { wordLists ->
                val wordList = wordLists.find {
                    it.name == appPreference.wordListName
                } ?: wordLists.firstOrNull()
                wordList?.let { _ ->
                    Timber.d("-_-_- initialize $wordList")
                    setWordList(wordList)
                } ?: Completable.complete()
            }
            .subscribeOn(Schedulers.io())
    }
}
