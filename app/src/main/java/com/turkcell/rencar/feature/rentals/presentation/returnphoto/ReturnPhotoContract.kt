package com.turkcell.rencar.feature.rentals.presentation.returnphoto

import android.graphics.Bitmap
import com.turkcell.rencar.feature.rentals.domain.model.RentalPhotoSide

data class ReturnPhotoState(
    val rentalId: String? = null,
    val photos: Map<RentalPhotoSide, Bitmap?> = RentalPhotoSide.values().associateWith { null },
    val allCaptured: Boolean = false,
    val isFinishing: Boolean = false,
    val error: String? = null
)

sealed interface ReturnPhotoEvent {
    data class ScreenOpened(val rentalId: String) : ReturnPhotoEvent
    data class PhotoCaptured(val side: RentalPhotoSide, val bitmap: Bitmap) : ReturnPhotoEvent
    data object ConfirmClicked : ReturnPhotoEvent
}

sealed interface ReturnPhotoEffect {
    data class NavigateToPayment(val rentalId: String) : ReturnPhotoEffect
    data class ShowError(val message: String) : ReturnPhotoEffect
}
