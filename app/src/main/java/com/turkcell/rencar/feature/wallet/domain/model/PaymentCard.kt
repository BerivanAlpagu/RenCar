package com.turkcell.rencar.feature.wallet.domain.model

data class PaymentCard(
    val id: String,
    val cardType: CardType,
    val lastFour: String,
    val expiryDate: String,
    val isDefault: Boolean
)
