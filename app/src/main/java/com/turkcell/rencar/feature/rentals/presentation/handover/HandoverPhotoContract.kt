package com.turkcell.rencar.feature.rentals.presentation.handover

import android.graphics.Bitmap
import com.turkcell.rencar.feature.rentals.domain.model.RentalPhotoSide
import java.io.File

data class HandoverPhotoState(
    val rentalId: String? = null,
    val vehicleLabel: String? = null,
    val photos: Map<RentalPhotoSide, Bitmap?> = RentalPhotoSide.values().associateWith { null },
    val uploadingSide: RentalPhotoSide? = null,
    val uploadedCount: Int = 0,
    val photosComplete: Boolean = false,
    val isStarting: Boolean = false,
    val isCancelling: Boolean = false,
    val error: String? = null
)

sealed interface HandoverPhotoEvent {
    data class ScreenOpened(val rentalId: String) : HandoverPhotoEvent
    data class PhotoCaptured(val side: RentalPhotoSide, val bitmap: Bitmap, val file: File) : HandoverPhotoEvent
    data object StartRentalClicked : HandoverPhotoEvent
    data object CancelClicked : HandoverPhotoEvent
}

sealed interface HandoverPhotoEffect {
    data class NavigateToActiveRental(val rentalId: String) : HandoverPhotoEffect
    data object NavigateBackToMap : HandoverPhotoEffect
    data class ShowError(val message: String) : HandoverPhotoEffect
}
