package com.turkcell.rencar.feature.reservations.data.di

import com.turkcell.rencar.feature.reservations.data.repository.DefaultReservationRepository
import com.turkcell.rencar.feature.reservations.domain.repository.ReservationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ReservationDataModule {

    @Binds
    @Singleton
    abstract fun bindReservationRepository(
        reservationRepositoryImpl: DefaultReservationRepository
    ): ReservationRepository
}
