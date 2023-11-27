package com.zerodeg.cleanarchitecture.di

import com.zerodeg.data.GithubRemoteSource
import com.zerodeg.data.GithubRemoteSourceImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    @Singleton
    @Binds
    abstract fun bindsGithubRemoteSource(source: GithubRemoteSourceImpl): GithubRemoteSource

}