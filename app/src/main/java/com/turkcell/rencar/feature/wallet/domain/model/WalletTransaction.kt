package com.turkcell.rencar.feature.wallet.domain.model

import java.time.LocalDateTime

data class WalletTransaction(
    val id: String,
    val title: String,
    val dateTime: LocalDateTime,
    val amount: Double
)
