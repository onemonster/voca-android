package com.tedilabs.voca.di

import android.app.Application
import com.squareup.moshi.Moshi
import com.tedilabs.voca.network.service.WordApiService
import com.tedilabs.voca.preference.AppPreference
import com.tedilabs.voca.repository.AppRepositoryManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    @Singleton
    fun appDatabaseManager(
        application: Application,
        wordApiService: WordApiService,
        appPreference: AppPreference,
        moshi: Moshi,
    ): AppRepositoryManager {
        return AppRepositoryManager(
            application,
            wordApiService,
            appPreference,
            moshi
        )
    }
}
