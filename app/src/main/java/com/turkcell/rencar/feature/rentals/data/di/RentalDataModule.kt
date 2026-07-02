package com.turkcell.rencar.feature.rentals.data.di

import com.turkcell.rencar.feature.rentals.data.repository.DefaultRentalRepository
import com.turkcell.rencar.feature.rentals.domain.repository.RentalRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RentalDataModule {

    @Binds
    @Singleton
    abstract fun bindRentalRepository(
        rentalRepositoryImpl: DefaultRentalRepository
    ): RentalRepository
}
