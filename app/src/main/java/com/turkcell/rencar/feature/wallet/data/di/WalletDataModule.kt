package com.turkcell.rencar.feature.wallet.data.di

import com.turkcell.rencar.feature.wallet.data.repository.DefaultWalletRepository
import com.turkcell.rencar.feature.wallet.domain.repository.WalletRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class WalletDataModule {

    @Binds
    @Singleton
    abstract fun bindWalletRepository(
        walletRepositoryImpl: DefaultWalletRepository
    ): WalletRepository
}
