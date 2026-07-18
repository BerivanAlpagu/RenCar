package com.turkcell.rencar.feature.wallet.data.remote

import com.turkcell.rencar.feature.wallet.data.remote.dto.CardResponseDto
import com.turkcell.rencar.feature.wallet.data.remote.dto.CreateCardDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface CardsApi {
    @GET("cards")
    suspend fun list(): Response<List<CardResponseDto>>

    @POST("cards")
    suspend fun create(@Body body: CreateCardDto): Response<CardResponseDto>

    @PATCH("cards/{id}/default")
    suspend fun setDefault(@Path("id") id: String): Response<CardResponseDto>

    @DELETE("cards/{id}")
    suspend fun remove(@Path("id") id: String): Response<Unit>
}
