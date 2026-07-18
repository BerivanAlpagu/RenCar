package com.turkcell.rencar.feature.wallet.data.remote

import com.turkcell.rencar.feature.wallet.data.remote.dto.TopupDto
import com.turkcell.rencar.feature.wallet.data.remote.dto.WalletResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface WalletApi {
    @GET("wallet")
    suspend fun getWallet(): Response<WalletResponseDto>

    @POST("wallet/topup")
    suspend fun topup(@Body body: TopupDto): Response<WalletResponseDto>
}
