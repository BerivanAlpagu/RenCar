package com.turkcell.rencar.feature.wallet.data.repository

import com.turkcell.rencar.feature.wallet.data.remote.CardsApi
import com.turkcell.rencar.feature.wallet.data.remote.WalletApi
import com.turkcell.rencar.feature.wallet.data.remote.dto.CardResponseDto
import com.turkcell.rencar.feature.wallet.data.remote.dto.CreateCardDto
import com.turkcell.rencar.feature.wallet.data.remote.dto.TopupDto
import com.turkcell.rencar.feature.wallet.data.remote.dto.WalletResponseDto
import com.turkcell.rencar.feature.wallet.data.remote.dto.WalletTransactionDto
import com.turkcell.rencar.feature.wallet.domain.model.CardType
import com.turkcell.rencar.feature.wallet.domain.model.PaymentCard
import com.turkcell.rencar.feature.wallet.domain.model.WalletInfo
import com.turkcell.rencar.feature.wallet.domain.model.WalletTransaction
import com.turkcell.rencar.feature.wallet.domain.repository.WalletRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultWalletRepository @Inject constructor(
    private val walletApi: WalletApi,
    private val cardsApi: CardsApi
) : WalletRepository {

    override fun getWalletInfoFlow(): Flow<WalletInfo> = flow {
        val walletResponse = walletApi.getWallet()
        val walletBody = walletResponse.body()
        if (!walletResponse.isSuccessful || walletBody == null) {
            throw Exception("Cüzdan bilgisi alınamadı: ${walletResponse.code()}")
        }
        val cardsResponse = cardsApi.list()
        val cards = if (cardsResponse.isSuccessful) {
            cardsResponse.body()?.map { it.toDomain() } ?: emptyList()
        } else {
            emptyList()
        }
        emit(walletBody.toDomain(cards))
    }

    override suspend fun addBalance(amount: Double): Result<Unit> {
        return try {
            val response = walletApi.topup(TopupDto(amount))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Bakiye yüklenemedi: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCards(): Result<List<PaymentCard>> {
        return try {
            val response = cardsApi.list()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.map { it.toDomain() })
            } else {
                Result.failure(Exception("Kartlar alınamadı: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addCard(
        brand: String,
        last4: String,
        expMonth: Int,
        expYear: Int
    ): Result<PaymentCard> {
        return try {
            val response = cardsApi.create(
                CreateCardDto(
                    brand = brand,
                    last4 = last4,
                    expMonth = expMonth,
                    expYear = expYear
                )
            )
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Kart eklenemedi: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

private val isoFormatter = DateTimeFormatter.ISO_DATE_TIME

private fun WalletTransactionDto.toDomain() = WalletTransaction(
    id = id,
    title = description,
    dateTime = runCatching { LocalDateTime.parse(createdAt, isoFormatter) }.getOrDefault(LocalDateTime.now()),
    amount = amount
)

private fun WalletResponseDto.toDomain(cards: List<PaymentCard>) = WalletInfo(
    balance = balance,
    cards = cards,
    transactions = transactions.map { it.toDomain() }
)

private fun CardResponseDto.toDomain() = PaymentCard(
    id = id,
    cardType = runCatching { CardType.valueOf(brand) }.getOrDefault(CardType.VISA),
    lastFour = last4,
    expiryDate = "%02d/%02d".format(expMonth, expYear % 100),
    isDefault = isDefault
)
