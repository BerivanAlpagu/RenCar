package com.turkcell.rencar.feature.rentals.data.remote

import com.turkcell.rencar.feature.rentals.data.remote.dto.CreateRentalDto
import com.turkcell.rencar.feature.rentals.data.remote.dto.RentalResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RentalApi {
    @POST("rentals")
    suspend fun createRental(@Body request: CreateRentalDto): Response<RentalResponseDto>
}
