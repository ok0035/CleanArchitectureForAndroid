package com.zerodeg.di

import android.content.Context
import com.zerodeg.util.PermissionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext

@Module
@InstallIn(ActivityComponent::class)
class PermissionModule {
    @Provides
    fun providePermissionManager(@ActivityContext context: Context): PermissionManager {
        return PermissionManager(context)
    }
}