package com.turkcell.rencar.feature.wallet.data.repository

import com.turkcell.rencar.feature.wallet.domain.model.CardType
import com.turkcell.rencar.feature.wallet.domain.model.PaymentCard
import com.turkcell.rencar.feature.wallet.domain.model.WalletInfo
import com.turkcell.rencar.feature.wallet.domain.model.WalletTransaction
import com.turkcell.rencar.feature.wallet.domain.repository.WalletRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultWalletRepository @Inject constructor() : WalletRepository {

    private val _walletState = MutableStateFlow(
        WalletInfo(
            balance = 340.00,
            cards = listOf(
                PaymentCard(
                    id = "c1",
                    cardType = CardType.VISA,
                    lastFour = "4291",
                    expiryDate = "08/27",
                    isDefault = true
                ),
                PaymentCard(
                    id = "c2",
                    cardType = CardType.MASTERCARD,
                    lastFour = "7740",
                    expiryDate = "11/26",
                    isDefault = false
                )
            ),
            transactions = listOf(
                WalletTransaction(
                    id = "t1",
                    title = "Renault Clio kiralama",
                    dateTime = LocalDateTime.now().withHour(14).withMinute(32),
                    amount = -110.50
                ),
                WalletTransaction(
                    id = "t2",
                    title = "Bakiye yükleme",
                    dateTime = LocalDateTime.now().minusDays(1).withHour(9).withMinute(10),
                    amount = 200.00
                )
            )
        )
    )

    override fun getWalletInfoFlow(): Flow<WalletInfo> {
        return _walletState.asStateFlow()
    }

    override suspend fun addBalance(amount: Double): Result<Unit> {
        _walletState.update { current ->
            val updatedBalance = current.balance + amount
            val newTransaction = WalletTransaction(
                id = UUID.randomUUID().toString(),
                title = "Bakiye yükleme",
                dateTime = LocalDateTime.now(),
                amount = amount
            )
            current.copy(
                balance = updatedBalance,
                transactions = listOf(newTransaction) + current.transactions
            )
        }
        return Result.success(Unit)
    }
}
