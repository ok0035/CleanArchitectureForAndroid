package com.zerodeg.di

import com.zerodeg.domain.interfaces.GithubRepository
import com.zerodeg.data.GithubRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindsGithubRepository(repository: GithubRepositoryImpl): GithubRepository

}