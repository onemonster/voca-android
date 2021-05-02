package com.tedilabs.voca.network.service.retrofit

import com.tedilabs.voca.network.dto.ApiInformationDto
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET

interface VersionRetrofitService {

    @GET("/")
    fun getApiInformation(): Single<ApiInformationDto>
}
