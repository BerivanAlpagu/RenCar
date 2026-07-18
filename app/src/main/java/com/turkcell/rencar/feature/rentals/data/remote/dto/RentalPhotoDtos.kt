package com.turkcell.rencar.feature.rentals.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class RentalPhotoDto(
    val side: String,
    val imageUrl: String,
    val createdAt: String
)

@Serializable
data class RentalPhotosStateDto(
    val rentalId: String,
    val photos: List<RentalPhotoDto>,
    val uploadedCount: Int,
    val remainingSides: List<String>,
    val photosComplete: Boolean
)
