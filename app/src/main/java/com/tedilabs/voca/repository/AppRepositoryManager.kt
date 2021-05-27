package com.tedilabs.voca.repository

import android.content.Context
import androidx.room.Room
import com.squareup.moshi.Moshi
import com.tedilabs.voca.model.WordList
import com.tedilabs.voca.network.service.WordApiService
import com.tedilabs.voca.preference.AppPreference
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.lang.Exception

class AppRepositoryManager(
    private val applicationContext: Context,
    private val wordApiService: WordApiService,
    private val appPreference: AppPreference,
    private val moshi: Moshi
) {

    private val wordRepositoryCache = mutableMapOf<String, WordRepository>()

    fun getWordRepository(wordList: WordList): Single<WordRepository> {
        val file = File(applicationContext.filesDir, wordList.dbUrl)
        return if (!file.exists()) {
            Timber.d("-_-_- getDatabase file does not exists! $file")
            wordApiService.download(wordList.url)
                .subscribeOn(Schedulers.io())
                .map { body ->
                    val input: InputStream = body.byteStream()
                    try {
                        file.createNewFile()
                        val fos = FileOutputStream(file)
                        fos.use { output ->
                            val buffer = ByteArray(4 * 1024)
                            var read: Int
                            while (input.read(buffer).also { read = it } != -1) {
                                output.write(buffer, 0, read)
                            }
                            output.flush()
                        }
                        input.close()
                    } catch (e: Exception) {
                        input.close()
                        throw e
                    }
                }
                .ignoreElement()
        } else {
            Timber.d("-_-_- getDatabase file exists! $file")
            Completable.complete()
        }
            .andThen(
                Single.defer {
                    val key = wordList.key
                    val wordRepository = wordRepositoryCache[key] ?: run {
                        val database = Room.databaseBuilder(
                            applicationContext,
                            AppDatabase::class.java,
                            wordList.dbUrl
                        )
                            .createFromFile(file)
                            .build()
                        val wordRepository = WordRepository(
                            wordList,
                            database.wordDao(),
                            appPreference,
                            moshi,
                        )
                        wordRepositoryCache[key] = wordRepository
                        wordRepository
                    }
                    Single.just(wordRepository)
                }
                    .subscribeOn(Schedulers.io())
            )
    }
}
