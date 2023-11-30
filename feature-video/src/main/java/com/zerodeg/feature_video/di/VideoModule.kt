package com.zerodeg.feature_video.di

import android.content.Context
import com.zerodeg.feature_video.utils.VideoUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object VideoModule {
    @Provides
    @Singleton
    fun providesTempFileGenerator(
        @ApplicationContext context: Context
    ) = VideoUtils(context)
}