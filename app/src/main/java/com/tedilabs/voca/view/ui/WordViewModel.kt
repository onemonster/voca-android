package com.tedilabs.voca.view.ui

import androidx.lifecycle.ViewModel
import com.tedilabs.voca.model.Word
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject

@HiltViewModel
class WordViewModel @Inject constructor() : ViewModel() {

    private val wordSubject = BehaviorSubject.createDefault(Word.default)

    fun observeWord(): Observable<Word> = wordSubject.hide()

    val word: Word = wordSubject.value

    fun showNextWord() {
        // TODO wordSubject.onNext(nextWord)
    }

    fun showPrevWord() {
        // TODO wordSubject.onNext(prevWord)
    }
}
