package com.tedilabs.voca.network.service

import com.github.zafarkhaja.semver.Version
import com.tedilabs.voca.BuildConfig
import com.tedilabs.voca.model.AppVersionStatus
import com.tedilabs.voca.network.service.retrofit.VersionRetrofitService
import io.reactivex.rxjava3.core.Single

class VersionApiService(private val versionRetrofitService: VersionRetrofitService) {

    fun getAppVersionStatus(): Single<AppVersionStatus> {
        return versionRetrofitService.getApiInformation()
            .map {
                AppVersionStatus(
                    currentVersion = Version.valueOf(BuildConfig.VERSION_NAME),
                    latestVersion = Version.valueOf(it.clients.android.latest),
                    versionRequirement = it.clients.android.required
                )
            }
    }
}
