package com.turkcell.rencar.feature.auth.data.di

import com.turkcell.rencar.feature.auth.data.repository.DefaultAuthRepository
import com.turkcell.rencar.feature.auth.domain.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthDataModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: DefaultAuthRepository
    ): AuthRepository
}
