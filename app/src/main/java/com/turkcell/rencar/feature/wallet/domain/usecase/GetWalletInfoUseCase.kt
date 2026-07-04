package com.turkcell.rencar.feature.wallet.domain.usecase

import com.turkcell.rencar.feature.wallet.domain.model.WalletInfo
import com.turkcell.rencar.feature.wallet.domain.repository.WalletRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWalletInfoUseCase @Inject constructor(
    private val repository: WalletRepository
) {
    operator fun invoke(): Flow<WalletInfo> {
        return repository.getWalletInfoFlow()
    }
}
