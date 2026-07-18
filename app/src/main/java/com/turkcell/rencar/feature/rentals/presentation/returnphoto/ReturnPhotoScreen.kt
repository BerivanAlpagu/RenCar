package com.turkcell.rencar.feature.rentals.presentation.returnphoto

import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.turkcell.rencar.core.ui.PhotoCaptureGrid
import com.turkcell.rencar.feature.rentals.domain.model.RentalPhotoSide

private fun RentalPhotoSide.label(): String = when (this) {
    RentalPhotoSide.FRONT -> "Ön"
    RentalPhotoSide.BACK -> "Arka"
    RentalPhotoSide.LEFT -> "Sol"
    RentalPhotoSide.RIGHT -> "Sağ"
}

@Composable
fun ReturnPhotoScreen(
    rentalId: String,
    viewModel: ReturnPhotoViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onFinishConfirmed: (String) -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    val bgColor = if (isDark) Color(0xFF0C0F14) else Color(0xFFF4F6F9)
    val topBarColor = if (isDark) Color(0xFF10151B) else Color(0xFFFFFFFF)
    val textColor = if (isDark) Color(0xFFF3F6FA) else Color(0xFF101620)
    val subTextColor = if (isDark) Color(0xFF98A2B0) else Color(0xFF5C6675)

    var currentSide by remember { mutableStateOf<RentalPhotoSide?>(null) }
    var showPickerDialog by remember { mutableStateOf(false) }

    LaunchedEffect(rentalId) {
        viewModel.onEvent(ReturnPhotoEvent.ScreenOpened(rentalId))
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ReturnPhotoEffect.NavigateToPayment -> onFinishConfirmed(effect.rentalId)
                is ReturnPhotoEffect.ShowError -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        val side = currentSide
        if (bitmap != null && side != null) {
            viewModel.onEvent(ReturnPhotoEvent.PhotoCaptured(side, bitmap))
        }
        showPickerDialog = false
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        val side = currentSide
        if (uri != null && side != null) {
            val bitmap = context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) }
            if (bitmap != null) {
                viewModel.onEvent(ReturnPhotoEvent.PhotoCaptured(side, bitmap))
            }
        }
        showPickerDialog = false
    }

    val takenCount = state.photos.values.count { it != null }

    if (showPickerDialog) {
        AlertDialog(
            onDismissRequest = { showPickerDialog = false },
            title = { Text(text = "Fotoğraf Yükle") },
            text = { Text("Lütfen fotoğraf çekmek veya seçmek için bir yöntem belirleyin.") },
            confirmButton = {
                TextButton(onClick = { cameraLauncher.launch(null) }) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Camera")
                    Spacer(Modifier.width(4.dp))
                    Text("Kamera")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = "Gallery")
                    Spacer(Modifier.width(4.dp))
                    Text("Galeri")
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = bgColor,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(topBarColor)
                    .padding(horizontal = 18.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(if (isDark) Color(0xFF1B212A) else Color(0xFFF1F4F8), RoundedCornerShape(13.dp))
                        .clickable { onBackClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = textColor
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = "Teslim fotoğrafları",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(topBarColor)
                    .padding(18.dp)
                    .padding(bottom = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 12.dp)) {
                    Icon(Icons.Default.Info, contentDescription = "Info", tint = Color(0xFFE6A700), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Teslim öncesi hasarları net çek — anlaşmazlığı önler.", fontSize = 11.5.sp, color = subTextColor, fontWeight = FontWeight.Medium)
                }
                Button(
                    onClick = { viewModel.onEvent(ReturnPhotoEvent.ConfirmClicked) },
                    enabled = state.allCaptured && !state.isFinishing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(if (isDark) 30.dp else 26.dp, RoundedCornerShape(18.dp), spotColor = Color(0x4D0B6BCB)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (state.allCaptured) Color(0xFF0B6BCB) else if (isDark) Color(0xFF222A33) else Color(0xFFE3E8EF),
                        contentColor = if (state.allCaptured) Color.White else if (isDark) Color(0xFF6B7480) else Color(0xFF9AA3AE)
                    ),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    if (state.isFinishing) {
                        CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                    } else {
                        Text(
                            text = if (state.allCaptured) "Teslimi Onayla" else "Teslimi Onayla · ${4 - takenCount} foto kaldı",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.5.sp
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(top = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Yolculuk bitti — teslim öncesi son adım", fontSize = 13.sp, color = subTextColor, fontWeight = FontWeight.SemiBold)
                Text("$takenCount / 4 çekildi", fontSize = 13.sp, color = Color(0xFF0B6BCB), fontWeight = FontWeight.ExtraBold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            PhotoCaptureGrid(
                sides = RentalPhotoSide.values().toList(),
                photos = state.photos,
                label = { it.label() },
                isDark = isDark,
                onSideClick = { side ->
                    currentSide = side
                    showPickerDialog = true
                },
                modifier = Modifier.padding(horizontal = 18.dp)
            )
        }
    }
}
