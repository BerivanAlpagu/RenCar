package com.turkcell.rencar.feature.vehicles.data.remote

import com.turkcell.rencar.feature.vehicles.data.remote.dto.QuoteResponseDto
import com.turkcell.rencar.feature.vehicles.data.remote.dto.VehicleResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface VehicleApi {
    @GET("/vehicles")
    suspend fun getVehicles(
        @Query("type") type: String? = null,
        @Query("segment") segment: String? = null,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
        @Query("includeBusy") includeBusy: Boolean? = null
    ): Response<List<VehicleResponseDto>>

    @GET("/vehicles/{id}")
    suspend fun getOne(@Path("id") id: String): Response<VehicleResponseDto>

    @GET("/vehicles/{id}/quote")
    suspend fun getQuote(
        @Path("id") id: String,
        @Query("plan") plan: String,
        @Query("minutes") minutes: Int
    ): Response<QuoteResponseDto>
}
