package com.turkcell.rencar.feature.profile.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import java.util.Locale

@Composable
fun ProfileScreen(
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val isDark = isSystemInDarkTheme()
    val background = if (isDark) Color(0xFF0C0F14) else Color(0xFFF4F6F9)
    val textPrimary = if (isDark) Color(0xFFF3F6FA) else Color(0xFF101620)
    val textSecondary = if (isDark) Color(0xFF98A2B0) else Color(0xFF5C6675)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(background)
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color(0xFF0B6BCB)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 24.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    ProfileHeader(
                        fullName = state.user?.fullName ?: "RenCar Kullanıcısı",
                        phone = state.user?.displayPhone() ?: "Telefon bilgisi yok",
                        textPrimary = textPrimary,
                        textSecondary = textSecondary,
                        isDark = isDark
                    )
                }

                item {
                    LicenseCard(status = state.licenseStatus, isDark = isDark)
                }

                state.stats?.let { stats ->
                    item {
                        StatsRow(
                            tripCount = stats.tripCount,
                            totalSpent = stats.totalSpent,
                            totalKm = stats.totalKm,
                            isDark = isDark
                        )
                    }
                }

                item {
                    ProfileMenuCard(isDark = isDark)
                }

                item {
                    LogoutButton(onLogoutClick = onLogoutClick, isDark = isDark)
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    fullName: String,
    phone: String,
    textPrimary: Color,
    textSecondary: Color,
    isDark: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(if (isDark) Color(0xFF17345A) else Color(0xFFEAF2FC)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = if (isDark) Color(0xFF4C95F0) else Color(0xFF0B6BCB),
                modifier = Modifier.size(34.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = fullName, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textPrimary)
            Text(
                text = phone,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = textSecondary,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isDark) Color(0xFF171C24) else Color.White,
            border = if (isDark) BorderStroke(1.dp, Color(0xFF232A33)) else null,
            shadowElevation = if (isDark) 0.dp else 3.dp,
            modifier = Modifier.size(38.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    tint = if (isDark) Color(0xFFB6BFCB) else Color(0xFF3A4452),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun LicenseCard(status: String, isDark: Boolean) {
    val approved = status == "APPROVED"
    val pending = status == "UNDER_REVIEW"
    val iconBg = when {
        approved -> if (isDark) Color(0xFF152C20) else Color(0xFFE7F4EC)
        pending -> if (isDark) Color(0xFF2E2A16) else Color(0xFFFFF4D6)
        else -> if (isDark) Color(0xFF2E1A1B) else Color(0xFFFBEDED)
    }
    val accent = when {
        approved -> if (isDark) Color(0xFF34C98A) else Color(0xFF1A9E63)
        pending -> Color(0xFFE4A11B)
        else -> if (isDark) Color(0xFFF0575B) else Color(0xFFE5484D)
    }

    ProfileCard(isDark = isDark) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = accent, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = when {
                        approved -> "Ehliyet doğrulandı"
                        pending -> "Ehliyet inceleniyor"
                        else -> "Ehliyet bekleniyor"
                    },
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color(0xFFF3F6FA) else Color(0xFF101620)
                )
                Text(
                    text = if (approved) "B sınıfı · geçerli" else "Kiralamadan önce onay gerekli",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isDark) Color(0xFF7A828F) else Color(0xFF8A929E)
                )
            }
            Badge(text = if (approved) "Onaylı" else if (pending) "Bekliyor" else "Eksik", accent = accent, isDark = isDark)
        }
    }
}

@Composable
private fun StatsRow(tripCount: Int, totalSpent: Double, totalKm: Double, isDark: Boolean) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        StatBox("Yolculuk", tripCount.toString(), isDark, Modifier.weight(1f))
        StatBox("Harcama", "₺${String.format(Locale("tr", "TR"), "%.0f", totalSpent)}", isDark, Modifier.weight(1f))
        StatBox("Mesafe", "${String.format(Locale("tr", "TR"), "%.1f", totalKm)} km", isDark, Modifier.weight(1f))
    }
}

@Composable
private fun StatBox(label: String, value: String, isDark: Boolean, modifier: Modifier = Modifier) {
    ProfileCard(isDark = isDark, modifier = modifier) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = if (isDark) Color(0xFFF3F6FA) else Color(0xFF101620))
            Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = if (isDark) Color(0xFF7A828F) else Color(0xFF8A929E))
        }
    }
}

@Composable
private fun ProfileMenuCard(isDark: Boolean) {
    ProfileCard(isDark = isDark, contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)) {
        Column {
            ProfileMenuRow(Icons.Default.CreditCard, "Ödeme yöntemleri", isDark, showDivider = true)
            ProfileMenuRow(Icons.Default.Settings, "Ayarlar", isDark, showDivider = true)
            ProfileMenuRow(Icons.Default.HelpOutline, "Yardım & destek", isDark, showDivider = true)
            ProfileMenuRow(Icons.Default.Redeem, "Davet et · ₺50 kazan", isDark, showDivider = false)
        }
    }
}

@Composable
private fun ProfileMenuRow(icon: ImageVector, title: String, isDark: Boolean, showDivider: Boolean) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clickable { },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = if (isDark) Color(0xFFB6BFCB) else Color(0xFF3A4452), modifier = Modifier.size(21.dp))
            Spacer(modifier = Modifier.width(13.dp))
            Text(title, modifier = Modifier.weight(1f), fontSize = 14.5.sp, fontWeight = FontWeight.SemiBold, color = if (isDark) Color(0xFFF3F6FA) else Color(0xFF101620))
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = if (isDark) Color(0xFF4A535F) else Color(0xFFC7CFDA), modifier = Modifier.size(20.dp))
        }
        if (showDivider) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(if (isDark) Color(0xFF232A33) else Color(0xFFF0F2F6))
            )
        }
    }
}

@Composable
private fun LogoutButton(onLogoutClick: () -> Unit, isDark: Boolean) {
    ProfileCard(isDark = isDark, modifier = Modifier.clickable(onClick = onLogoutClick)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Logout, contentDescription = null, tint = if (isDark) Color(0xFFF0575B) else Color(0xFFE5484D), modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(9.dp))
            Text("Çıkış yap", fontSize = 14.5.sp, fontWeight = FontWeight.Bold, color = if (isDark) Color(0xFFF0575B) else Color(0xFFE5484D))
        }
    }
}

@Composable
private fun Badge(text: String, accent: Color, isDark: Boolean) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isDark) accent.copy(alpha = 0.18f) else accent.copy(alpha = 0.12f))
            .padding(horizontal = 9.dp, vertical = 4.dp)
    ) {
        Text(text, color = accent, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
private fun ProfileCard(
    isDark: Boolean,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(14.dp),
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(18.dp)
    Card(
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF171C24) else Color.White),
        border = if (isDark) BorderStroke(1.dp, Color(0xFF232A33)) else null,
        modifier = modifier
            .fillMaxWidth()
            .shadow(if (isDark) 0.dp else 3.dp, shape, clip = false)
    ) {
        Box(modifier = Modifier.padding(contentPadding)) {
            content()
        }
    }
}
