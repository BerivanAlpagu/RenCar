package com.turkcell.rencar.feature.wallet.domain.usecase

import com.turkcell.rencar.feature.wallet.domain.model.PaymentCard
import com.turkcell.rencar.feature.wallet.domain.repository.WalletRepository
import javax.inject.Inject

class AddCardUseCase @Inject constructor(
    private val repository: WalletRepository
) {
    suspend operator fun invoke(
        brand: String,
        last4: String,
        expMonth: Int,
        expYear: Int
    ): Result<PaymentCard> {
        return repository.addCard(brand, last4, expMonth, expYear)
    }
}
