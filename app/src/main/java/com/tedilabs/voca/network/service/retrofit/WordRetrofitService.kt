package com.tedilabs.voca.network.service.retrofit

import com.tedilabs.voca.network.dto.WordListsDto
import io.reactivex.rxjava3.core.Single
import okhttp3.ResponseBody
import retrofit2.http.*

interface WordRetrofitService {

    @GET("/{target-language}/word-lists")
    fun getWordLists(
        @Path("target-language") targetLanguage: String,
        @Query("source-language") sourceLanguage: String
    ): Single<WordListsDto>

    @Streaming
    @GET
    fun download(@Url fileUrl: String): Single<ResponseBody>
}
