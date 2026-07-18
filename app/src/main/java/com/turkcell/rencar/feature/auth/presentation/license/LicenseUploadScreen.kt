package com.turkcell.rencar.feature.auth.presentation.license

import android.graphics.Bitmap
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import com.turkcell.rencar.core.ui.toLocalFile
import java.io.File

@Composable
fun LicenseUploadScreen(
    viewModel: LicenseViewModel,
    onGoToApproval: () -> Unit
) {
    val status by viewModel.status.collectAsState()
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()

    var frontFile by remember { mutableStateOf<File?>(null) }
    var backFile by remember { mutableStateOf<File?>(null) }
    var selfieFile by remember { mutableStateOf<File?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    var uploadError by remember { mutableStateOf<String?>(null) }
    var activeSide by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.refreshStatus()
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        val target = activeSide ?: return@rememberLauncherForActivityResult
        if (bitmap != null) {
            val file = bitmap.toLocalFile(context, "license_$target")
            when (target) {
                "front" -> frontFile = file
                "back" -> backFile = file
                "selfie" -> selfieFile = file
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        val target = activeSide ?: return@rememberLauncherForActivityResult
        if (uri != null) {
            val file = uri.toLocalFile(context, "license_$target")
            when (target) {
                "front" -> frontFile = file
                "back" -> backFile = file
                "selfie" -> selfieFile = file
            }
        }
    }

    val screenBg = if (isDark) Color(0xFF0C0F14) else Color(0xFFF4F6F9)
    val textPrimary = if (isDark) Color(0xFFF3F6FA) else Color(0xFF101620)
    val textSecondary = if (isDark) Color(0xFF98A2B0) else Color(0xFF5C6675)
    val cardBg = if (isDark) Color(0xFF171C24) else Color.White
    val softCard = if (isDark) Color(0xFF14233A) else Color(0xFFEAF2FC)
    val borderColor = if (isDark) Color(0xFF2C333D) else Color(0xFFC7D0DB)
    val buttonBlue = Color(0xFF0B6BCB)
    val accentGreen = Color(0xFF1FB370)

    val uploadedCount = listOf(frontFile, backFile, selfieFile).count { it != null }
    val allReady = uploadedCount == 3

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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when (status) {
                            "UNDER_REVIEW" -> "Ehliyetin incelemede. Admin onayı bekleniyor."
                            "REJECTED" -> "Ehliyet reddedildi, dosyaları yeniden yükleyebilirsin."
                            "APPROVED" -> "Ehliyet onaylandı."
                            else -> "İlk girişten sonra ehliyet + selfie'ni bir kez yüklemen gerekiyor."
                        },
                        color = textSecondary,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .background(if (allReady) Color(0xFFE7F4EC) else softCard, RoundedCornerShape(8.dp))
                            .padding(horizontal = 9.dp, vertical = 4.dp)
                    ) {
                        Text(
                            "$uploadedCount / 3",
                            color = if (allReady) accentGreen else buttonBlue,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                SectionTitle("Ehliyet ön yüz", textPrimary)
                PickerCard(
                    isDark = isDark,
                    hasFile = frontFile != null,
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
                    hasFile = backFile != null,
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

                SectionTitle("Yüz doğrulama (selfie)", textPrimary)
                SelfiePickerCard(
                    isDark = isDark,
                    hasFile = selfieFile != null,
                    borderColor = borderColor,
                    cardBg = cardBg,
                    softCard = softCard,
                    accentGreen = accentGreen,
                    onCameraClick = {
                        activeSide = "selfie"
                        cameraLauncher.launch(null)
                    },
                    onGalleryClick = {
                        activeSide = "selfie"
                        galleryLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                )

                if (uploadError != null) {
                    Text(
                        text = uploadError!!,
                        color = Color(0xFFE5484D),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

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
                enabled = allReady,
                remainingCount = 3 - uploadedCount,
                onClick = {
                    uploadError = null
                    isUploading = true
                    viewModel.upload(
                        frontFile = frontFile,
                        backFile = backFile,
                        selfieFile = selfieFile,
                        onSuccess = {
                            isUploading = false
                            onGoToApproval()
                        },
                        onError = { message ->
                            isUploading = false
                            uploadError = message
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
            .border(width = 2.dp, color = if (hasFile) Color(0xFF1FB370) else borderColor, shape = RoundedCornerShape(18.dp)),
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
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF1FB370), modifier = Modifier.size(22.dp))
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
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = Color(0xFF0B6BCB), modifier = Modifier.size(18.dp))
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

/**
 * Selfie kartı bilinçli olarak dairesel/farklı tasarlandı — ön/arka ehliyet fotoğraflarından
 * (dikdörtgen belge çekimi) ayrışan, "yüz doğrulama" olduğunu görsel olarak anlatan bir kart.
 */
@Composable
private fun SelfiePickerCard(
    isDark: Boolean,
    hasFile: Boolean,
    borderColor: Color,
    cardBg: Color,
    softCard: Color,
    accentGreen: Color,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(cardBg)
            .border(width = 2.dp, color = if (hasFile) accentGreen else borderColor, shape = RoundedCornerShape(18.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(if (hasFile) Color(0xFFE7F4EC) else softCard),
            contentAlignment = Alignment.Center
        ) {
            if (hasFile) {
                Icon(Icons.Default.Check, contentDescription = null, tint = accentGreen, modifier = Modifier.size(28.dp))
            } else {
                Icon(Icons.Default.Face, contentDescription = null, tint = Color(0xFF0B6BCB), modifier = Modifier.size(28.dp))
            }
        }
        Spacer(modifier = Modifier.size(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (hasFile) "Selfie yüklendi" else "Yüzünü net gösteren bir selfie çek",
                color = if (hasFile) accentGreen else if (isDark) Color(0xFF98A2B0) else Color(0xFF5C6675),
                fontSize = 12.5.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onCameraClick,
                    modifier = Modifier.height(34.dp),
                    shape = RoundedCornerShape(11.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0B6BCB))
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.size(6.dp))
                    Text("Kamera", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onGalleryClick,
                    modifier = Modifier.height(34.dp),
                    shape = RoundedCornerShape(11.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDark) Color(0xFF222A33) else Color(0xFFE8EEF8),
                        contentColor = if (isDark) Color(0xFFF3F6FA) else Color(0xFF0B6BCB)
                    )
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.size(6.dp))
                    Text("Upload", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
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
    enabled: Boolean,
    remainingCount: Int,
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
            enabled = enabled && !isUploading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonBlue,
                disabledContainerColor = if (isDark) Color(0xFF222A33) else Color(0xFFE3E8EF)
            )
        ) {
            if (isUploading) {
                CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
            } else {
                Text(
                    text = if (enabled) "Devam Et" else "Devam Et · $remainingCount foto kaldı",
                    fontSize = 16.5.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
