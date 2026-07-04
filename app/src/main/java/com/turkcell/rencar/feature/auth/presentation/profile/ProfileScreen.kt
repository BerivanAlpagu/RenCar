package com.turkcell.rencar.feature.auth.presentation.profile

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.turkcell.rencar.feature.auth.domain.model.LicenseStatus
import com.turkcell.rencar.feature.auth.domain.model.UserLicenseStatus
import com.turkcell.rencar.feature.auth.domain.model.UserProfile

@Composable
fun ProfileScreen(
    onLogoutClick: () -> Unit,
    isDark: Boolean,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ProfileEffect.ShowSnackbar -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                is ProfileEffect.NavigateToOnboarding -> {
                    onLogoutClick()
                }
            }
        }
    }

    val backgroundColor = if (isDark) Color(0xFF0C0F14) else Color(0xFFF4F6F9)
    val textColor = if (isDark) Color(0xFFF3F6FA) else Color(0xFF101620)
    val subTextColor = if (isDark) Color(0xFF98A2B0) else Color(0xFF5C6675)
    val cardBackground = if (isDark) Color(0xFF171C24) else Color(0xFFFFFFFF)
    val cardBorder = if (isDark) Color(0xFF232A33) else Color.Transparent
    val shadowColor = if (isDark) Color.Transparent else Color(0x10101828)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color(0xFF0B6BCB)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Title
                Text(
                    text = "Profil",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    letterSpacing = (-0.5).sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Profile Header Item
                    item {
                        ProfileHeader(
                            profile = state.userProfile,
                            isDark = isDark,
                            cardBackground = cardBackground,
                            cardBorder = cardBorder,
                            shadowColor = shadowColor,
                            textColor = textColor,
                            subTextColor = subTextColor
                        )
                    }

                    // License Card Item
                    item {
                        LicenseStatusCard(
                            licenseStatus = state.licenseStatus,
                            isDark = isDark,
                            cardBackground = cardBackground,
                            cardBorder = cardBorder,
                            shadowColor = shadowColor,
                            textColor = textColor
                        )
                    }

                    // Menu Options Card Item
                    item {
                        MenuOptionsCard(
                            isDark = isDark,
                            cardBackground = cardBackground,
                            cardBorder = cardBorder,
                            shadowColor = shadowColor,
                            textColor = textColor
                        )
                    }

                    // Logout Button Item
                    item {
                        LogoutCardButton(
                            onClick = { viewModel.onEvent(ProfileEvent.LogoutClicked) },
                            isDark = isDark,
                            cardBackground = cardBackground,
                            cardBorder = cardBorder,
                            shadowColor = shadowColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileHeader(
    profile: UserProfile?,
    isDark: Boolean,
    cardBackground: Color,
    cardBorder: Color,
    shadowColor: Color,
    textColor: Color,
    subTextColor: Color
) {
    val name = profile?.fullName ?: "Kullanıcı"
    val phone = profile?.phone ?: "+90 --- --- -- --"

    val initials = if (name.isNotBlank()) {
        val parts = name.trim().split("\\s+".toRegex())
        if (parts.size >= 2) {
            "${parts[0].firstOrNull() ?: ""}${parts[1].firstOrNull() ?: ""}".uppercase()
        } else {
            "${name.firstOrNull() ?: ""}".uppercase()
        }
    } else {
        "?"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = if (isDark) 0.dp else 4.dp, shape = RoundedCornerShape(18.dp), ambientColor = shadowColor)
            .background(cardBackground, shape = RoundedCornerShape(18.dp))
            .border(width = 1.dp, color = cardBorder, shape = RoundedCornerShape(18.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Avatar slot
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(if (isDark) Color(0xFF232A33) else Color(0xFFE2E8F0)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color(0xFFF3F6FA) else Color(0xFF4A5568)
            )
        }

        // Details
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = phone,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = subTextColor
            )
        }

        // Edit Icon Box
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(if (isDark) Color(0xFF171C24) else Color(0xFFFFFFFF), shape = RoundedCornerShape(12.dp))
                .border(
                    width = 1.dp,
                    color = if (isDark) Color(0xFF232A33) else Color(0x1F000000),
                    shape = RoundedCornerShape(12.dp)
                )
                .shadow(elevation = if (isDark) 0.dp else 2.dp, shape = RoundedCornerShape(12.dp), ambientColor = shadowColor)
                .clickable { /* Edit Profile Action */ },
            contentAlignment = Alignment.Center
        ) {
            val density = LocalDensity.current.density
            val strokeColor = if (isDark) Color(0xFFB6BFCB) else Color(0xFF3A4452)
            Canvas(modifier = Modifier.size(18.dp)) {
                val scaleX = size.width / 24f
                val scaleY = size.height / 24f
                val penPath = Path().apply {
                    // M4 20l4-1 9.5-9.5a2.1 2.1 0 0 0-3-3L5 16l-1 4Z
                    moveTo(4f * scaleX, 20f * scaleY)
                    lineTo(8f * scaleX, 19f * scaleY)
                    lineTo(17.5f * scaleX, 9.5f * scaleY)
                    lineTo(14.5f * scaleX, 6.5f * scaleY)
                    lineTo(5f * scaleX, 16f * scaleY)
                    close()
                }
                drawPath(
                    path = penPath,
                    color = strokeColor,
                    style = Stroke(width = 1.8f * density, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
            }
        }
    }
}

private data class LicenseCardUIModel(
    val title: String,
    val subtitle: String,
    val badgeText: String,
    val themeColor: Color,
    val badgeBg: Color,
    val iconBg: Color
)

@Composable
fun LicenseStatusCard(
    licenseStatus: UserLicenseStatus?,
    isDark: Boolean,
    cardBackground: Color,
    cardBorder: Color,
    shadowColor: Color,
    textColor: Color
) {
    val status = licenseStatus?.status ?: LicenseStatus.NOT_SUBMITTED

    val cardUiModel = when (status) {
        LicenseStatus.APPROVED -> {
            val tc = if (isDark) Color(0xFF34C98A) else Color(0xFF1A9E63)
            val bg = if (isDark) Color(0xFF173726) else Color(0xFFE7F4EC)
            val ic = if (isDark) Color(0xFF152C20) else Color(0xFFE7F4EC)
            LicenseCardUIModel("Ehliyet doğrulandı", "B sınıfı · geçerli", "Onaylı", tc, bg, ic)
        }
        LicenseStatus.UNDER_REVIEW -> {
            val tc = if (isDark) Color(0xFFFBBF24) else Color(0xFFD97706)
            val bg = if (isDark) Color(0xFF332300) else Color(0xFFFFF9E6)
            val ic = if (isDark) Color(0xFF2A1C00) else Color(0xFFFFF9E6)
            LicenseCardUIModel("Ehliyet inceleniyor", "Onay bekleniyor", "İncelemede", tc, bg, ic)
        }
        LicenseStatus.REJECTED -> {
            val reason = licenseStatus?.rejectReason ?: "Görsel doğrulanamadı"
            val tc = if (isDark) Color(0xFFF0575B) else Color(0xFFE5484D)
            val bg = if (isDark) Color(0xFF2A1616) else Color(0xFFFDE8E8)
            val ic = if (isDark) Color(0xFF241212) else Color(0xFFFDE8E8)
            LicenseCardUIModel("Ehliyet reddedildi", reason, "Reddedildi", tc, bg, ic)
        }
        LicenseStatus.NOT_SUBMITTED -> {
            val tc = if (isDark) Color(0xFF9CA3AF) else Color(0xFF6B7280)
            val bg = if (isDark) Color(0xFF1F2937) else Color(0xFFF3F4F6)
            val ic = if (isDark) Color(0xFF1E252E) else Color(0xFFF3F4F6)
            LicenseCardUIModel("Ehliyet yüklenmedi", "Sürüş için ehliyet yükleyin", "Yüklenmedi", tc, bg, ic)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = if (isDark) 0.dp else 4.dp, shape = RoundedCornerShape(18.dp), ambientColor = shadowColor)
            .background(cardBackground, shape = RoundedCornerShape(18.dp))
            .border(width = 1.dp, color = cardBorder, shape = RoundedCornerShape(18.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Icon Container
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(cardUiModel.iconBg, shape = RoundedCornerShape(13.dp)),
            contentAlignment = Alignment.Center
        ) {
            val density = LocalDensity.current.density
            val strokeColor = cardUiModel.themeColor
            Canvas(modifier = Modifier.size(22.dp)) {
                val scaleX = size.width / 24f
                val scaleY = size.height / 24f
                val shieldPath = Path().apply {
                    moveTo(12f * scaleX, 3f * scaleY)
                    lineTo(19f * scaleX, 6f * scaleY)
                    lineTo(19f * scaleX, 11f * scaleY)
                    cubicTo(19f * scaleX, 15.5f * scaleY, 16f * scaleX, 19f * scaleY, 12f * scaleX, 21f * scaleY)
                    cubicTo(8f * scaleX, 19f * scaleY, 5f * scaleX, 15.5f * scaleY, 5f * scaleX, 11f * scaleY)
                    lineTo(5f * scaleX, 6f * scaleY)
                    close()
                }
                drawPath(
                    path = shieldPath,
                    color = strokeColor,
                    style = Stroke(width = 1.8f * density, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )

                if (status == LicenseStatus.APPROVED) {
                    val checkPath = Path().apply {
                        moveTo(9f * scaleX, 12f * scaleY)
                        lineTo(11f * scaleX, 14f * scaleY)
                        lineTo(15f * scaleX, 10f * scaleY)
                    }
                    drawPath(
                        path = checkPath,
                        color = strokeColor,
                        style = Stroke(width = 1.8f * density, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                } else if (status == LicenseStatus.REJECTED) {
                    // Draw small cross/X
                    drawLine(color = strokeColor, start = Offset(10f * scaleX, 10f * scaleY), end = Offset(14f * scaleX, 14f * scaleY), strokeWidth = 1.8f * density, cap = StrokeCap.Round)
                    drawLine(color = strokeColor, start = Offset(14f * scaleX, 10f * scaleY), end = Offset(10f * scaleX, 14f * scaleY), strokeWidth = 1.8f * density, cap = StrokeCap.Round)
                } else if (status == LicenseStatus.UNDER_REVIEW) {
                    // Draw exclamation mark/dots
                    drawLine(color = strokeColor, start = Offset(12f * scaleX, 9f * scaleY), end = Offset(12f * scaleX, 13f * scaleY), strokeWidth = 1.8f * density, cap = StrokeCap.Round)
                    drawCircle(color = strokeColor, radius = 1f * density, center = Offset(12f * scaleX, 15.5f * scaleY))
                }
            }
        }

        // Status Texts
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = cardUiModel.title,
                fontSize = 14.5.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = cardUiModel.subtitle,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = if (status == LicenseStatus.APPROVED) Color(0xFF8A929E) else cardUiModel.themeColor
            )
        }

        // Badge Status
        Box(
            modifier = Modifier
                .background(cardUiModel.badgeBg, shape = RoundedCornerShape(8.dp))
                .padding(vertical = 4.dp, horizontal = 9.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = cardUiModel.badgeText,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = cardUiModel.themeColor
            )
        }
    }
}

@Composable
fun MenuOptionsCard(
    isDark: Boolean,
    cardBackground: Color,
    cardBorder: Color,
    shadowColor: Color,
    textColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = if (isDark) 0.dp else 4.dp, shape = RoundedCornerShape(18.dp), ambientColor = shadowColor)
            .background(cardBackground, shape = RoundedCornerShape(18.dp))
            .border(width = 1.dp, color = cardBorder, shape = RoundedCornerShape(18.dp))
            .padding(vertical = 4.dp, horizontal = 16.dp)
    ) {
        val strokeColor = if (isDark) Color(0xFFB6BFCB) else Color(0xFF3A4452)
        val dividerColor = if (isDark) Color(0xFF232A33) else Color(0xFFF0F2F6)

        // Item 1: Ödeme yöntemleri (Card)
        MenuRowItem(
            label = "Ödeme yöntemleri",
            isDark = isDark,
            dividerColor = dividerColor,
            textColor = textColor,
            drawIcon = { color ->
                Canvas(modifier = Modifier.size(20.dp)) {
                    val scaleX = size.width / 24f
                    val scaleY = size.height / 24f
                    val rectPath = Path().apply {
                        addRoundRect(RoundRect(Rect(3f * scaleX, 6f * scaleY, 21f * scaleX, 19f * scaleY), 3f * scaleX, 3f * scaleY))
                    }
                    drawPath(path = rectPath, color = color, style = Stroke(width = 1.8f * density))
                    drawLine(color = color, start = Offset(3f * scaleX, 10f * scaleY), end = Offset(21f * scaleX, 10f * scaleY), strokeWidth = 1.8f * density)
                }
            }
        )

        // Item 2: Ayarlar (Gear)
        MenuRowItem(
            label = "Ayarlar",
            isDark = isDark,
            dividerColor = dividerColor,
            textColor = textColor,
            drawIcon = { color ->
                Canvas(modifier = Modifier.size(20.dp)) {
                    val scaleX = size.width / 24f
                    val scaleY = size.height / 24f
                    drawCircle(color = color, radius = 3f * scaleX, center = Offset(12f * scaleX, 12f * scaleY), style = Stroke(width = 1.8f * density))
                    val outerCircle = Path().apply {
                        // Outer gear-like shape or a clean concentric outline
                        moveTo(19f * scaleX, 12f * scaleY)
                        cubicTo(19f * scaleX, 11f * scaleY, 21f * scaleX, 9.5f * scaleY, 19f * scaleX, 6.1f * scaleY)
                        lineTo(16.7f * scaleX, 7.1f * scaleY)
                        // A simplified gear path for standard display
                        arcTo(Rect(5f * scaleX, 5f * scaleY, 19f * scaleX, 19f * scaleY), 0f, 359f, false)
                    }
                    drawPath(path = outerCircle, color = color, style = Stroke(width = 1.4f * density))
                }
            }
        )

        // Item 3: Yardım & destek (Help question mark)
        MenuRowItem(
            label = "Yardım & destek",
            isDark = isDark,
            dividerColor = dividerColor,
            textColor = textColor,
            drawIcon = { color ->
                Canvas(modifier = Modifier.size(20.dp)) {
                    val scaleX = size.width / 24f
                    val scaleY = size.height / 24f
                    drawCircle(color = color, radius = 9f * scaleX, center = Offset(12f * scaleX, 12f * scaleY), style = Stroke(width = 1.8f * density))
                    // Question mark line drawing
                    val qPath = Path().apply {
                        moveTo(12f * scaleX, 14f * scaleY)
                        cubicTo(12f * scaleX, 12f * scaleY, 14.5f * scaleX, 12f * scaleY, 14.5f * scaleX, 10f * scaleY)
                        arcTo(Rect(9.5f * scaleX, 7.5f * scaleY, 14.5f * scaleX, 12.5f * scaleY), 0f, -180f, false)
                    }
                    drawPath(path = qPath, color = color, style = Stroke(width = 1.8f * density, cap = StrokeCap.Round))
                    drawCircle(color = color, radius = 0.9f * density, center = Offset(12f * scaleX, 16.5f * scaleY))
                }
            }
        )

        // Item 4: Davet et · ₺50 kazan
        MenuRowItem(
            label = "Davet et · ₺50 kazan",
            isDark = isDark,
            dividerColor = Color.Transparent,
            textColor = textColor,
            drawIcon = { color ->
                Canvas(modifier = Modifier.size(20.dp)) {
                    val scaleX = size.width / 24f
                    val scaleY = size.height / 24f
                    val invitePath = Path().apply {
                        // M12 3a9 9 0 1 0 0 18M16 8l4 4-4 4M9 12h11
                        moveTo(12f * scaleX, 3f * scaleY)
                        arcTo(Rect(3f * scaleX, 3f * scaleY, 21f * scaleX, 21f * scaleY), 270f, -180f, false)
                        moveTo(16f * scaleX, 8f * scaleY)
                        lineTo(20f * scaleX, 12f * scaleY)
                        lineTo(16f * scaleX, 16f * scaleY)
                        moveTo(9f * scaleX, 12f * scaleY)
                        lineTo(20f * scaleX, 12f * scaleY)
                    }
                    drawPath(path = invitePath, color = color, style = Stroke(width = 1.8f * density, cap = StrokeCap.Round, join = StrokeJoin.Round))
                }
            }
        )
    }
}

@Composable
fun MenuRowItem(
    label: String,
    isDark: Boolean,
    dividerColor: Color,
    textColor: Color,
    drawIcon: @Composable (Color) -> Unit
) {
    val strokeColor = if (isDark) Color(0xFFB6BFCB) else Color(0xFF3A4452)
    val chevronColor = if (isDark) Color(0xFF4A535F) else Color(0xFFC7CFDA)

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { /* Menu Actions */ }
                .padding(vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(13.dp)
        ) {
            drawIcon(strokeColor)
            Text(
                text = label,
                fontSize = 14.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor,
                modifier = Modifier.weight(1f)
            )
            // Chevron icon
            Canvas(modifier = Modifier.size(18.dp)) {
                val scaleX = size.width / 24f
                val scaleY = size.height / 24f
                val chevronPath = Path().apply {
                    // M9 6l6 6-6 6
                    moveTo(9f * scaleX, 6f * scaleY)
                    lineTo(15f * scaleX, 12f * scaleY)
                    lineTo(9f * scaleX, 18f * scaleY)
                }
                drawPath(
                    path = chevronPath,
                    color = chevronColor,
                    style = Stroke(width = 2f * density, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
            }
        }
        if (dividerColor != Color.Transparent) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(dividerColor)
            )
        }
    }
}

@Composable
fun LogoutCardButton(
    onClick: () -> Unit,
    isDark: Boolean,
    cardBackground: Color,
    cardBorder: Color,
    shadowColor: Color
) {
    val redColor = if (isDark) Color(0xFFF0575B) else Color(0xFFE5484D)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = if (isDark) 0.dp else 4.dp, shape = RoundedCornerShape(16.dp), ambientColor = shadowColor)
            .background(cardBackground, shape = RoundedCornerShape(16.dp))
            .border(width = 1.dp, color = cardBorder, shape = RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Canvas(modifier = Modifier.size(19.dp)) {
            val scaleX = size.width / 24f
            val scaleY = size.height / 24f
            val logoutPath = Path().apply {
                // M15 4h2a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2h-2M10 8l-4 4 4 4M6 12h11
                moveTo(15f * scaleX, 4f * scaleY)
                lineTo(17f * scaleX, 4f * scaleY)
                cubicTo(18.1f * scaleX, 4f * scaleY, 19f * scaleX, 4.9f * scaleY, 19f * scaleX, 6f * scaleY)
                lineTo(19f * scaleX, 18f * scaleY)
                cubicTo(19f * scaleX, 19.1f * scaleY, 18.1f * scaleX, 20f * scaleY, 17f * scaleX, 20f * scaleY)
                lineTo(15f * scaleX, 20f * scaleY)

                moveTo(10f * scaleX, 8f * scaleY)
                lineTo(6f * scaleX, 12f * scaleY)
                lineTo(10f * scaleX, 16f * scaleY)

                moveTo(6f * scaleX, 12f * scaleY)
                lineTo(17f * scaleX, 12f * scaleY)
            }
            drawPath(
                path = logoutPath,
                color = redColor,
                style = Stroke(width = 1.8f * density, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }
        Spacer(modifier = Modifier.width(9.dp))
        Text(
            text = "Çıkış yap",
            fontSize = 14.5.sp,
            fontWeight = FontWeight.Bold,
            color = redColor,
            textAlign = TextAlign.Center
        )
    }
}
