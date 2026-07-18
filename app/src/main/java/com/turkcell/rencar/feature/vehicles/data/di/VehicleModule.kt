package com.turkcell.rencar.feature.vehicles.data.di

import com.turkcell.rencar.feature.vehicles.data.remote.VehicleApi
import com.turkcell.rencar.feature.vehicles.data.repository.DefaultVehicleRepository
import com.turkcell.rencar.feature.vehicles.domain.repository.VehicleRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class VehicleModule {

    @Binds
    @Singleton
    abstract fun bindVehicleRepository(defaultVehicleRepository: DefaultVehicleRepository): VehicleRepository

    companion object {
        @Provides
        @Singleton
        fun provideVehicleApi(retrofit: Retrofit): VehicleApi {
            return retrofit.create(VehicleApi::class.java)
        }
    }
}
