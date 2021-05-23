package com.tedilabs.voca.repository

import com.squareup.moshi.Moshi
import com.tedilabs.voca.model.Word
import com.tedilabs.voca.model.WordList
import com.tedilabs.voca.preference.AppPreference
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber

class WordRepository(
    private val wordList: WordList,
    private val wordDao: WordDao,
    private val appPreference: AppPreference,
    private val moshi: Moshi
) {
    companion object {
        private const val PAGE_SIZE = 5
    }

    val wordCount = wordDao.getCount()
    private var wordCache = emptyList<Word>()
    private var cursor: Int
        get() = appPreference.getCursor(wordList)
        set(value) {
            appPreference.setCursor(wordList, value)
        }

    fun getCurrent(): Single<Word> {
        return wordCache.find { it.id == cursor + 1 }?.let { cachedWord ->
            Single.just(cachedWord)
        } ?: run {
            wordDao.getNext(cursor, PAGE_SIZE)
                .subscribeOn(Schedulers.io())
                .map { wordEntities ->
                    wordCache = wordEntities.toWords(moshi)
                    Timber.d("-_-_- wordDao getCurrent ${wordCache.map { "${it.id} ${it.word}" }}")
                    wordCache.first()
                }
        }
    }

    fun getNext(): Single<Word> {
        return (wordCache.find { it.id == cursor + 1 }?.let { cachedWord ->
            Single.just(cachedWord)
        } ?: run {
            wordDao.getNext(cursor, PAGE_SIZE)
                .subscribeOn(Schedulers.io())
                .map { wordEntities ->
                    wordCache = wordEntities.toWords(moshi)
                    Timber.d("-_-_- wordDao getNext ${wordCache.map { "${it.id} ${it.word}" }}")
                    wordCache.first()
                }
        })
            .doAfterSuccess {
                cursor = it.id
            }
    }

    fun getPrev(): Single<Word> {
        return (wordCache.find { it.id == cursor - 1 }?.let { cachedWord ->
            Single.just(cachedWord)
        } ?: run {
            wordDao.getPrev(cursor, PAGE_SIZE)
                .subscribeOn(Schedulers.io())
                .map { wordEntities ->
                    wordCache = wordEntities.reversed().toWords(moshi)
                    Timber.d("-_-_- wordDao getPrev ${wordCache.map { "${it.id} ${it.word}" }}")
                    wordCache.last()
                }
        })
            .doAfterSuccess {
                cursor = it.id
            }
    }
}
