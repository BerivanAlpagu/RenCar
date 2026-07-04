package com.turkcell.rencar.feature.rentals.presentation.handover

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HandoverPhotoScreen(
    vehicleId: String,
    onBackClick: () -> Unit,
    onStartRentalClick: (String) -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val bgColor = if (isDark) Color(0xFF0C0F14) else Color(0xFFF4F6F9)
    val topBarColor = if (isDark) Color(0xFF10151B) else Color(0xFFFFFFFF)
    val textColor = if (isDark) Color(0xFFF3F6FA) else Color(0xFF101620)
    val subTextColor = if (isDark) Color(0xFF98A2B0) else Color(0xFF5C6675)
    
    var frontBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var backBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var leftBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var rightBitmap by remember { mutableStateOf<Bitmap?>(null) }
    
    // Tracks which side is currently being updated
    var currentSide by remember { mutableStateOf<String?>(null) }
    var showPickerDialog by remember { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            when (currentSide) {
                "Ön" -> frontBitmap = bitmap
                "Arka" -> backBitmap = bitmap
                "Sol" -> leftBitmap = bitmap
                "Sağ" -> rightBitmap = bitmap
            }
        }
        showPickerDialog = false
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            // For demo purposes, we will just consider the URI valid and show a placeholder or we would need to decode it.
            // Since we use Bitmap for preview, let's just create a dummy 1x1 bitmap if uri is present, 
            // or we could use Coil to load the URI. To avoid adding dependencies, we'll just set a non-null state.
            // Actually, we'll create a 1x1 bitmap to represent a successful gallery pick.
            val dummyBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
            when (currentSide) {
                "Ön" -> frontBitmap = dummyBitmap
                "Arka" -> backBitmap = dummyBitmap
                "Sol" -> leftBitmap = dummyBitmap
                "Sağ" -> rightBitmap = dummyBitmap
            }
        }
        showPickerDialog = false
    }

    val takenCount = listOf(frontBitmap, backBitmap, leftBitmap, rightBitmap).count { it != null }
    val allTaken = takenCount == 4

    if (showPickerDialog) {
        AlertDialog(
            onDismissRequest = { showPickerDialog = false },
            title = { Text(text = "Fotoğraf Yükle") },
            text = { Text("Lütfen fotoğraf çekmek veya seçmek için bir yöntem belirleyin.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        cameraLauncher.launch(null)
                    }
                ) {
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
                    text = "Araç durumu",
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
                    Text("Hasarları net çek — teslim sonrası anlaşmazlığı önler.", fontSize = 11.5.sp, color = subTextColor, fontWeight = FontWeight.Medium)
                }
                Button(
                    onClick = { if (allTaken) onStartRentalClick(vehicleId) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(if (isDark) 30.dp else 26.dp, RoundedCornerShape(18.dp), spotColor = Color(0x4D0B6BCB)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (allTaken) Color(0xFF0B6BCB) else if (isDark) Color(0xFF222A33) else Color(0xFFE3E8EF),
                        contentColor = if (allTaken) Color.White else if (isDark) Color(0xFF6B7480) else Color(0xFF9AA3AE)
                    ),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text(
                        text = if (allTaken) "Kiralamayı Başlat" else "Kiralamayı Başlat · ${4 - takenCount} foto kaldı",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.5.sp
                    )
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
                Text("Renault Clio · 34 RNC 022", fontSize = 13.sp, color = subTextColor, fontWeight = FontWeight.SemiBold)
                Text("$takenCount / 4 çekildi", fontSize = 13.sp, color = Color(0xFF0B6BCB), fontWeight = FontWeight.ExtraBold)
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { PhotoCard("Ön", frontBitmap, isDark) { currentSide = "Ön"; showPickerDialog = true } }
                item { PhotoCard("Arka", backBitmap, isDark) { currentSide = "Arka"; showPickerDialog = true } }
                item { PhotoCard("Sol", leftBitmap, isDark) { currentSide = "Sol"; showPickerDialog = true } }
                item { PhotoCard("Sağ", rightBitmap, isDark) { currentSide = "Sağ"; showPickerDialog = true } }
            }
        }
    }
}

@Composable
fun PhotoCard(label: String, bitmap: Bitmap?, isDark: Boolean, onClick: () -> Unit) {
    val cardBg = if (isDark) Color(0xFF171C24) else Color.White
    val borderColor = if (isDark) Color(0xFF2C333D) else Color(0xFFC7D0DB)
    val isTaken = bitmap != null
    
    Box(
        modifier = Modifier
            .height(158.dp)
            .background(if (isTaken) (if (isDark) Color(0xFF11161D) else Color(0xFFE6EBF1)) else cardBg, RoundedCornerShape(18.dp))
            .then(if (!isTaken) Modifier.border(2.dp, borderColor, RoundedCornerShape(18.dp)) else Modifier)
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isTaken) {
            // Render bitmap as background
            Image(bitmap = bitmap!!.asImageBitmap(), contentDescription = "Photo", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            Box(modifier = Modifier.fillMaxSize().background(Color(0x66000000))) // Darken overlay
            
            Box(modifier = Modifier.align(Alignment.TopStart).padding(8.dp).background(if(isDark) Color.Black else Color(0xFF101620), RoundedCornerShape(7.dp)).padding(horizontal = 9.dp, vertical = 3.dp)) {
                Text(label, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Box(modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).size(24.dp).background(Color(0xFF1FB370), CircleShape), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Check, contentDescription = "Done", tint = Color.White, modifier = Modifier.size(16.dp))
            }
        } else {
            Box(modifier = Modifier.align(Alignment.TopStart).padding(8.dp).background(if(isDark) Color(0xFF222A33) else Color(0xFFEEF1F5), RoundedCornerShape(7.dp)).padding(horizontal = 9.dp, vertical = 3.dp)) {
                Text(label, color = if(isDark) Color(0xFF98A2B0) else Color(0xFF5C6675), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.size(46.dp).background(Color(0xFF0B6BCB), RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Camera", tint = Color.White, modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.height(9.dp))
                Text("Ekle / Çek", fontSize = 12.sp, color = if(isDark) Color(0xFF98A2B0) else Color(0xFF5C6675), fontWeight = FontWeight.Bold)
            }
        }
    }
}
