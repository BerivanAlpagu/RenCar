package com.turkcell.rencar.app.di

import android.app.Application
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.turkcell.rencar.core.location.DefaultLocationTracker
import com.turkcell.rencar.core.location.LocationTracker
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocationModule {

    @Binds
    @Singleton
    abstract fun bindLocationTracker(defaultLocationTracker: DefaultLocationTracker): LocationTracker

    companion object {
        @Provides
        @Singleton
        fun provideFusedLocationProviderClient(
            application: Application
        ): FusedLocationProviderClient {
            return LocationServices.getFusedLocationProviderClient(application)
        }
    }
}
