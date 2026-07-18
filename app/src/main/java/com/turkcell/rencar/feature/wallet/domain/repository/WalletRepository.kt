package com.turkcell.rencar.feature.wallet.domain.repository

import com.turkcell.rencar.feature.wallet.domain.model.PaymentCard
import com.turkcell.rencar.feature.wallet.domain.model.WalletInfo
import kotlinx.coroutines.flow.Flow

interface WalletRepository {
    fun getWalletInfoFlow(): Flow<WalletInfo>
    suspend fun addBalance(amount: Double): Result<Unit>
    suspend fun getCards(): Result<List<PaymentCard>>
}
