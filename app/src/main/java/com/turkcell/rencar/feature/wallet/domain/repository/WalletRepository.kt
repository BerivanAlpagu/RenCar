package com.turkcell.rencar.feature.wallet.domain.repository

import com.turkcell.rencar.feature.wallet.domain.model.WalletInfo
import kotlinx.coroutines.flow.Flow

interface WalletRepository {
    fun getWalletInfoFlow(): Flow<WalletInfo>
    suspend fun addBalance(amount: Double): Result<Unit>
}
