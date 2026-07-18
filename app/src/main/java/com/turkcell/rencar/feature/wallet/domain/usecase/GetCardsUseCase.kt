package com.turkcell.rencar.feature.wallet.domain.usecase

import com.turkcell.rencar.feature.wallet.domain.model.PaymentCard
import com.turkcell.rencar.feature.wallet.domain.repository.WalletRepository
import javax.inject.Inject

class GetCardsUseCase @Inject constructor(
    private val repository: WalletRepository
) {
    suspend operator fun invoke(): Result<List<PaymentCard>> {
        return repository.getCards()
    }
}
