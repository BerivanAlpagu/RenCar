package com.turkcell.rencar.feature.reservations.data.remote

import com.turkcell.rencar.feature.reservations.data.remote.dto.CreateReservationDto
import com.turkcell.rencar.feature.reservations.data.remote.dto.ReservationResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ReservationApi {
    @POST("reservations")
    suspend fun create(@Body body: CreateReservationDto): Response<ReservationResponseDto>

    @GET("reservations/active")
    suspend fun getActive(): Response<ReservationResponseDto>

    @DELETE("reservations/{id}")
    suspend fun cancel(@Path("id") id: String): Response<Unit>
}
