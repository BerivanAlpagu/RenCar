package com.turkcell.rencar.feature.auth.presentation.license

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.rencar.feature.auth.data.local.TokenManager
import com.turkcell.rencar.feature.auth.data.remote.LicenseApi
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class LicenseViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val licenseApi: LicenseApi
) : ViewModel() {

    private val _status = MutableStateFlow<String>("NOT_SUBMITTED")
    val status = _status.asStateFlow()

    fun refreshStatus() {
        viewModelScope.launch {
            runCatching { licenseApi.getStatus() }
                .onSuccess { _status.value = it.status }
        }
    }

    fun upload(frontPath: String?, backPath: String?, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (frontPath.isNullOrBlank() || backPath.isNullOrBlank()) {
            onError("Lütfen ön ve arka yüz fotoğrafını seç.")
            return
        }

        viewModelScope.launch {
            runCatching {
                val frontFile = File(frontPath)
                val backFile = File(backPath)
                val front = MultipartBody.Part.createFormData(
                    "front",
                    frontFile.name,
                    frontFile.asRequestBody("image/jpeg".toMediaType())
                )
                val back = MultipartBody.Part.createFormData(
                    "back",
                    backFile.name,
                    backFile.asRequestBody("image/jpeg".toMediaType())
                )
                licenseApi.upload(front, back)
            }.onSuccess {
                _status.value = "UNDER_REVIEW"
                onSuccess()
            }.onFailure {
                onError(it.localizedMessage ?: "Ehliyet yüklenemedi.")
            }
        }
    }
}
