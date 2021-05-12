package com.tedilabs.voca.network.service

import com.tedilabs.voca.BuildConfig
import com.tedilabs.voca.model.WordList
import com.tedilabs.voca.network.service.retrofit.WordRetrofitService
import io.reactivex.rxjava3.core.Single
import java.util.*

class WordApiService(private val wordRetrofitService: WordRetrofitService) {

    fun getWordLists(sourceLocale: Locale = Locale.getDefault()): Single<List<WordList>> {
        return wordRetrofitService.getWordLists(
            targetLanguage = BuildConfig.TARGET_LANGUAGE_PATH,
            sourceLanguage = sourceLocale.language
        )
            .map { wordListsDto ->
                wordListsDto.data.map { wordListDto ->
                    WordList(
                        name = wordListDto.name,
                        sourceLanguage = wordListDto.sourceLanguage,
                        targetLanguage = wordListDto.targetLanguage,
                        version = wordListDto.version,
                        url = wordListDto.url
                    )
                }
            }
    }
}
