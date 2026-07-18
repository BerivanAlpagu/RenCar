package com.turkcell.rencar.feature.rentals.presentation.handover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.rencar.feature.rentals.domain.model.RentalPhotoSide
import com.turkcell.rencar.feature.rentals.domain.repository.RentalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HandoverPhotoViewModel @Inject constructor(
    private val rentalRepository: RentalRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HandoverPhotoState())
    val state = _state.asStateFlow()

    private val _effect = Channel<HandoverPhotoEffect>()
    val effect = _effect.receiveAsFlow()

    fun onEvent(event: HandoverPhotoEvent) {
        when (event) {
            is HandoverPhotoEvent.ScreenOpened -> loadExisting(event.rentalId)
            is HandoverPhotoEvent.PhotoCaptured -> uploadPhoto(event.side, event.bitmap, event.file)
            is HandoverPhotoEvent.StartRentalClicked -> startRental()
            is HandoverPhotoEvent.CancelClicked -> cancelRental()
        }
    }

    private fun loadExisting(rentalId: String) {
        if (_state.value.rentalId == rentalId) return
        _state.update { it.copy(rentalId = rentalId) }
        viewModelScope.launch {
            rentalRepository.getRental(rentalId).onSuccess { rental ->
                _state.update { it.copy(vehicleLabel = "${rental.vehicle.brand} ${rental.vehicle.model} · ${rental.vehicle.plate}") }
            }
            rentalRepository.getPhotosState(rentalId).onSuccess { photosState ->
                _state.update {
                    it.copy(
                        uploadedCount = photosState.uploadedCount,
                        photosComplete = photosState.photosComplete
                    )
                }
            }
        }
    }

    private fun uploadPhoto(side: RentalPhotoSide, bitmap: android.graphics.Bitmap, file: java.io.File) {
        val rentalId = _state.value.rentalId ?: return
        viewModelScope.launch {
            _state.update {
                it.copy(uploadingSide = side, photos = it.photos + (side to bitmap))
            }
            rentalRepository.uploadPhoto(rentalId, side, file)
                .onSuccess { photosState ->
                    _state.update {
                        it.copy(
                            uploadingSide = null,
                            uploadedCount = photosState.uploadedCount,
                            photosComplete = photosState.photosComplete
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(uploadingSide = null, photos = it.photos + (side to null))
                    }
                    _effect.send(HandoverPhotoEffect.ShowError(error.message ?: "Fotoğraf yüklenemedi"))
                }
        }
    }

    private fun startRental() {
        val rentalId = _state.value.rentalId ?: return
        if (!_state.value.photosComplete || _state.value.isStarting) return
        viewModelScope.launch {
            _state.update { it.copy(isStarting = true) }
            rentalRepository.startRental(rentalId)
                .onSuccess {
                    _state.update { it.copy(isStarting = false) }
                    _effect.send(HandoverPhotoEffect.NavigateToActiveRental(rentalId))
                }
                .onFailure { error ->
                    _state.update { it.copy(isStarting = false) }
                    _effect.send(HandoverPhotoEffect.ShowError(error.message ?: "Yolculuk başlatılamadı"))
                }
        }
    }

    private fun cancelRental() {
        val rentalId = _state.value.rentalId
        if (rentalId == null) {
            viewModelScope.launch { _effect.send(HandoverPhotoEffect.NavigateBackToMap) }
            return
        }
        if (_state.value.isCancelling) return
        viewModelScope.launch {
            _state.update { it.copy(isCancelling = true) }
            rentalRepository.cancelRental(rentalId)
            _state.update { it.copy(isCancelling = false) }
            _effect.send(HandoverPhotoEffect.NavigateBackToMap)
        }
    }
}
