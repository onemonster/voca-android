package com.tedilabs.voca.network.service.retrofit

import com.tedilabs.voca.network.dto.WordListsDto
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WordRetrofitService {

    @GET("/{target-language}/word-lists")
    fun getWordLists(
        @Path("target-language") targetLanguage: String,
        @Query("source-language") sourceLanguage: String
    ): Single<WordListsDto>
}
