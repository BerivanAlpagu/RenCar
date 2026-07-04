package com.turkcell.rencar.feature.wallet.domain.usecase

import com.turkcell.rencar.feature.wallet.domain.repository.WalletRepository
import javax.inject.Inject

class AddBalanceUseCase @Inject constructor(
    private val repository: WalletRepository
) {
    suspend operator fun invoke(amount: Double): Result<Unit> {
        return if (amount <= 0) {
            Result.failure(IllegalArgumentException("Yüklenecek miktar sıfırdan büyük olmalıdır."))
        } else {
            repository.addBalance(amount)
        }
    }
}
