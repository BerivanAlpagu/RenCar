package com.turkcell.rencar.feature.vehicles.data.remote

import com.turkcell.rencar.feature.vehicles.data.remote.dto.VehicleResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface VehicleApi {
    @GET("/vehicles")
    suspend fun getVehicles(
        @Query("type") type: String? = null,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): Response<List<VehicleResponseDto>>
}
