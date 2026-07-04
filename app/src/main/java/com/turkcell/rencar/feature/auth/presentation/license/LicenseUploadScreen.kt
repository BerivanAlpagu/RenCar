package com.turkcell.rencar.feature.auth.presentation.license

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

@Composable
fun LicenseUploadScreen(
    viewModel: LicenseViewModel,
    onGoToApproval: () -> Unit
) {
    val status by viewModel.status.collectAsState()
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()

    var frontPath by remember { mutableStateOf<String?>(null) }
    var backPath by remember { mutableStateOf<String?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    var activeSide by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.refreshStatus()
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        val target = activeSide ?: return@rememberLauncherForActivityResult
        if (bitmap != null) {
            val path = bitmap.toLocalFile(context, "license_$target")
            if (target == "front") frontPath = path else backPath = path
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        val target = activeSide ?: return@rememberLauncherForActivityResult
        if (uri != null) {
            val path = uri.toLocalFile(context, "license_$target")
            if (target == "front") frontPath = path else backPath = path
        }
    }

    val screenBg = if (isDark) Color(0xFF0C0F14) else Color(0xFFF4F6F9)
    val textPrimary = if (isDark) Color(0xFFF3F6FA) else Color(0xFF101620)
    val textSecondary = if (isDark) Color(0xFF98A2B0) else Color(0xFF5C6675)
    val cardBg = if (isDark) Color(0xFF171C24) else Color.White
    val softCard = if (isDark) Color(0xFF14233A) else Color(0xFFEAF2FC)
    val borderColor = if (isDark) Color(0xFF2C333D) else Color(0xFFC7D0DB)
    val buttonBlue = Color(0xFF0B6BCB)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(screenBg)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(16.dp))

            Header(
                isDark = isDark,
                title = "Ehliyet doğrulama",
                subtitle = "Kiralamadan önce tek seferlik",
                textPrimary = textPrimary,
                textSecondary = textSecondary
            )

            Stepper(
                isDark = isDark,
                textPrimary = textPrimary,
                inactiveText = textSecondary
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 18.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = when (status) {
                        "UNDER_REVIEW" -> "Ehliyetin incelemede. Admin onayı bekleniyor."
                        "REJECTED" -> "Ehliyet reddedildi, dosyayı yeniden yükleyebilirsin."
                        "APPROVED" -> "Ehliyet onaylandı."
                        else -> "İlk girişten sonra ehliyetini bir kez yüklemen gerekiyor."
                    },
                    color = textSecondary,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Medium
                )

                SectionTitle("Ehliyet ön yüz", textPrimary)
                PickerCard(
                    isDark = isDark,
                    hasFile = frontPath != null,
                    title = "Ön yüzü çek veya yükle",
                    borderColor = borderColor,
                    cardBg = cardBg,
                    softCard = softCard,
                    onCameraClick = {
                        activeSide = "front"
                        cameraLauncher.launch(null)
                    },
                    onGalleryClick = {
                        activeSide = "front"
                        galleryLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                )

                SectionTitle("Ehliyet arka yüz", textPrimary)
                PickerCard(
                    isDark = isDark,
                    hasFile = backPath != null,
                    title = "Arka yüzü çek veya yükle",
                    borderColor = borderColor,
                    cardBg = cardBg,
                    softCard = softCard,
                    onCameraClick = {
                        activeSide = "back"
                        cameraLauncher.launch(null)
                    },
                    onGalleryClick = {
                        activeSide = "back"
                        galleryLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                )

                InfoCard(
                    isDark = isDark,
                    softCard = softCard,
                    text = "Bilgilerin güvenle saklanır. Doğrulama genelde birkaç dakika sürer.",
                    textColor = if (isDark) Color(0xFF9CC3F0) else Color(0xFF2C5A8C)
                )
            }

            BottomAction(
                isDark = isDark,
                buttonBlue = buttonBlue,
                isUploading = isUploading,
                onClick = {
                    isUploading = true
                    viewModel.upload(
                        frontPath = frontPath,
                        backPath = backPath,
                        onSuccess = {
                            isUploading = false
                            onGoToApproval()
                        },
                        onError = {
                            isUploading = false
                        }
                    )
                }
            )
        }
    }
}

@Composable
private fun Header(
    isDark: Boolean,
    title: String,
    subtitle: String,
    textPrimary: Color,
    textSecondary: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(if (isDark) Color(0xFF171C24) else Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text("‹", color = textPrimary, fontSize = 28.sp, fontWeight = FontWeight.SemiBold)
        }
        Spacer(modifier = Modifier.size(14.dp))
        Column {
            Text(title, color = textPrimary, fontSize = 19.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = textSecondary, fontSize = 12.5.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun Stepper(
    isDark: Boolean,
    textPrimary: Color,
    inactiveText: Color
) {
    val lineColor = if (isDark) Color(0xFF2C333D) else Color(0xFFE0E5EC)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StepItem("1", "Ehliyet", true, textPrimary, inactiveText)
        Spacer(Modifier.weight(1f))
        Box(Modifier.weight(1f).height(2.dp).background(lineColor))
        Spacer(Modifier.weight(1f))
        StepItem("2", "Onay", false, textPrimary, inactiveText)
    }
}

@Composable
private fun StepItem(
    number: String,
    label: String,
    active: Boolean,
    textPrimary: Color,
    inactiveText: Color
) {
    val circleBg = if (active) Color(0xFF0B6BCB) else Color(0xFFE3E8EF)
    val circleText = if (active) Color.White else Color(0xFF8A929E)
    val labelColor = if (active) textPrimary else inactiveText
    val labelWeight = if (active) FontWeight.Bold else FontWeight.SemiBold

    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(circleBg),
            contentAlignment = Alignment.Center
        ) {
            Text(number, color = circleText, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
        }
        Spacer(modifier = Modifier.size(6.dp))
        Text(label, color = labelColor, fontSize = 11.5.sp, fontWeight = labelWeight)
    }
}

@Composable
private fun SectionTitle(text: String, color: Color) {
    Text(
        text = text,
        color = color,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 2.dp, bottom = 2.dp)
    )
}

@Composable
private fun PickerCard(
    isDark: Boolean,
    hasFile: Boolean,
    title: String,
    borderColor: Color,
    cardBg: Color,
    softCard: Color,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(118.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(cardBg)
            .border(width = 2.dp, color = borderColor, shape = RoundedCornerShape(18.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (hasFile) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(13.dp))
                        .background(if (isDark) Color(0xFF14233A) else Color(0xFFEAF2FC)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("✓", color = Color(0xFF1FB370), fontSize = 22.sp, fontWeight = FontWeight.Black)
                }
                Spacer(modifier = Modifier.height(7.dp))
                Text("Yüklendi", color = Color(0xFF1FB370), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(13.dp))
                        .background(softCard),
                    contentAlignment = Alignment.Center
                ) {
                    Text("▣", color = Color(0xFF0B6BCB), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                Text(
                    text = title,
                    color = if (isDark) Color(0xFF98A2B0) else Color(0xFF5C6675),
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = onCameraClick,
                        modifier = Modifier.height(36.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0B6BCB))
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.size(6.dp))
                        Text("Kamera", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = onGalleryClick,
                        modifier = Modifier.height(36.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDark) Color(0xFF222A33) else Color(0xFFE8EEF8),
                            contentColor = if (isDark) Color(0xFFF3F6FA) else Color(0xFF0B6BCB)
                        )
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.size(6.dp))
                        Text("Upload", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoCard(
    isDark: Boolean,
    softCard: Color,
    text: String,
    textColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(softCard)
            .padding(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(CircleShape)
                .background(Color.Transparent)
                .border(1.6.dp, if (isDark) Color(0xFF4C95F0) else Color(0xFF0B6BCB), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("i", color = if (isDark) Color(0xFF4C95F0) else Color(0xFF0B6BCB), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.size(10.dp))
        Text(text = text, color = textColor, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun BottomAction(
    isDark: Boolean,
    buttonBlue: Color,
    isUploading: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isDark) Color(0xFF10151B) else Color.White)
            .padding(start = 18.dp, end = 18.dp, top = 14.dp, bottom = 30.dp)
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = buttonBlue)
        ) {
            if (isUploading) {
                CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
            } else {
                Text("Devam Et", fontSize = 16.5.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

private fun Bitmap.toLocalFile(context: android.content.Context, prefix: String): String? {
    return runCatching {
        val file = File.createTempFile(prefix, ".jpg", context.cacheDir)
        ByteArrayOutputStream().use { stream ->
            compress(Bitmap.CompressFormat.JPEG, 90, stream)
            FileOutputStream(file).use { output -> output.write(stream.toByteArray()) }
        }
        file.absolutePath
    }.getOrNull()
}

private fun Uri.toLocalFile(context: android.content.Context, prefix: String): String? {
    return runCatching {
        val input = context.contentResolver.openInputStream(this) ?: return null
        val bitmap = BitmapFactory.decodeStream(input) ?: return null
        val file = File.createTempFile(prefix, ".jpg", context.cacheDir)
        ByteArrayOutputStream().use { stream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
            FileOutputStream(file).use { output -> output.write(stream.toByteArray()) }
        }
        file.absolutePath
    }.getOrNull()
}
