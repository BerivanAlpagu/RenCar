package com.turkcell.rencar.feature.rentals.presentation.returnphoto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.rencar.feature.rentals.domain.repository.RentalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Teslim (dönüş) fotoğrafları backend'e YÜKLENMEZ — POST /rentals/{id}/photos yalnız
 * PREPARING aşamasında kabul ediliyor, teslim sonrası karşılığı yok. Bu ekran UX
 * tutarlılığı için 4 fotoğrafı yerelde toplar, ardından doğrudan finish çağrılır.
 */
@HiltViewModel
class ReturnPhotoViewModel @Inject constructor(
    private val rentalRepository: RentalRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ReturnPhotoState())
    val state = _state.asStateFlow()

    private val _effect = Channel<ReturnPhotoEffect>()
    val effect = _effect.receiveAsFlow()

    fun onEvent(event: ReturnPhotoEvent) {
        when (event) {
            is ReturnPhotoEvent.ScreenOpened -> {
                _state.update { it.copy(rentalId = event.rentalId) }
            }
            is ReturnPhotoEvent.PhotoCaptured -> {
                val updatedPhotos = _state.value.photos + (event.side to event.bitmap)
                _state.update {
                    it.copy(photos = updatedPhotos, allCaptured = updatedPhotos.values.all { bitmap -> bitmap != null })
                }
            }
            is ReturnPhotoEvent.ConfirmClicked -> finish()
        }
    }

    private fun finish() {
        val rentalId = _state.value.rentalId ?: return
        if (!_state.value.allCaptured || _state.value.isFinishing) return
        viewModelScope.launch {
            _state.update { it.copy(isFinishing = true) }
            rentalRepository.finishRental(rentalId)
                .onSuccess {
                    _state.update { it.copy(isFinishing = false) }
                    _effect.send(ReturnPhotoEffect.NavigateToPayment(rentalId))
                }
                .onFailure { error ->
                    _state.update { it.copy(isFinishing = false) }
                    _effect.send(ReturnPhotoEffect.ShowError(error.message ?: "Yolculuk bitirilemedi"))
                }
        }
    }
}
