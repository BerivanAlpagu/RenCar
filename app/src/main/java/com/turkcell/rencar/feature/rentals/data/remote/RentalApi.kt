package com.turkcell.rencar.feature.rentals.data.remote

import com.turkcell.rencar.feature.rentals.data.remote.dto.ActiveRentalResponseDto
import com.turkcell.rencar.feature.rentals.data.remote.dto.CreateRentalDto
import com.turkcell.rencar.feature.rentals.data.remote.dto.FinishRentalResponseDto
import com.turkcell.rencar.feature.rentals.data.remote.dto.PayRentalDto
import com.turkcell.rencar.feature.rentals.data.remote.dto.PayRentalResponseDto
import com.turkcell.rencar.feature.rentals.data.remote.dto.RentalPhotosStateDto
import com.turkcell.rencar.feature.rentals.data.remote.dto.RentalResponseDto
import com.turkcell.rencar.feature.rentals.data.remote.dto.RentalStatsResponseDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface RentalApi {
    @POST("rentals")
    suspend fun createRental(@Body request: CreateRentalDto): Response<RentalResponseDto>

    @GET("rentals")
    suspend fun listMine(): Response<List<RentalResponseDto>>

    @GET("rentals/stats")
    suspend fun getStats(@Query("month") month: String? = null): Response<RentalStatsResponseDto>

    @GET("rentals/active")
    suspend fun getActive(): Response<ActiveRentalResponseDto>

    @GET("rentals/{id}")
    suspend fun getOne(@Path("id") id: String): Response<RentalResponseDto>

    @Multipart
    @POST("rentals/{id}/photos")
    suspend fun uploadPhoto(
        @Path("id") id: String,
        @Part("side") side: RequestBody,
        @Part file: MultipartBody.Part
    ): Response<RentalPhotosStateDto>

    @GET("rentals/{id}/photos")
    suspend fun getPhotos(@Path("id") id: String): Response<RentalPhotosStateDto>

    @POST("rentals/{id}/start")
    suspend fun start(@Path("id") id: String): Response<RentalResponseDto>

    @POST("rentals/{id}/finish")
    suspend fun finish(@Path("id") id: String): Response<FinishRentalResponseDto>

    @POST("rentals/{id}/pay")
    suspend fun pay(@Path("id") id: String, @Body body: PayRentalDto): Response<PayRentalResponseDto>

    @DELETE("rentals/{id}")
    suspend fun cancel(@Path("id") id: String): Response<Unit>
}
