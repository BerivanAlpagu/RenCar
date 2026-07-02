package com.turkcell.rencar.feature.wallet.domain.model

data class WalletInfo(
    val balance: Double,
    val cards: List<PaymentCard>,
    val transactions: List<WalletTransaction>
)
