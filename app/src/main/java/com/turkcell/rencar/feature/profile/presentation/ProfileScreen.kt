package com.turkcell.rencar.feature.profile.presentation

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.CardGiftcard
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.ExitToApp
import androidx.compose.material.icons.rounded.HelpOutline
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Payment
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ProfileScreen(
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ProfileEffect.ShowError -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Detecting dark mode consistent with the theme in WalletScreen.kt
    val isDark = MaterialTheme.colorScheme.background == Color(0xFF121212)
    
    val backgroundColor = if (isDark) Color(0xFF0C0F14) else Color(0xFFF4F6F9)
    val cardBackground = if (isDark) Color(0xFF171C24) else Color(0xFFFFFFFF)
    val cardBorder = if (isDark) BorderStroke(1.dp, Color(0xFF232A33)) else null
    val textPrimary = if (isDark) Color(0xFFF3F6FA) else Color(0xFF101620)
    val textSecondary = if (isDark) Color(0xFF98A2B0) else Color(0xFF5C6675)
    val dividerColor = if (isDark) Color(0xFF232A33) else Color(0xFFF0F2F6)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // 1. Profile Info Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Profile Avatar (Premium Gradient initials circle)
                    val initials = getInitials(state.fullName)
                    val avatarGradient = Brush.linearGradient(
                        colors = if (isDark) {
                            listOf(Color(0xFF2479DC), Color(0xFF0B5AAE))
                        } else {
                            listOf(Color(0xFF1E7FE0), Color(0xFF0B6BCB))
                        }
                    )
                    
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(avatarGradient)
                    ) {
                        Text(
                            text = initials,
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    // Name and Phone details
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = state.fullName.ifBlank { "Misafir Kullanıcı" },
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = state.phone.ifBlank { "Telefon girilmemiş" },
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = textSecondary,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    // Edit Button on the right
                    val editBorder = if (isDark) BorderStroke(1.dp, Color(0xFF232A33)) else null
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(38.dp)
                            .shadow(
                                elevation = if (isDark) 0.dp else 2.dp,
                                shape = RoundedCornerShape(12.dp),
                                clip = false
                            )
                            .clip(RoundedCornerShape(12.dp))
                            .background(cardBackground)
                            .then(if (editBorder != null) Modifier.border(editBorder, RoundedCornerShape(12.dp)) else Modifier)
                            .clickable {
                                Toast.makeText(context, "Profil düzenleme yakında eklenecektir.", Toast.LENGTH_SHORT).show()
                            }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = "Edit Profile",
                            tint = if (isDark) Color(0xFFB6BFCB) else Color(0xFF3A4452),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 2. Driver's License Status Card
                val badgeConfig = getLicenseBadgeConfig(state.licenseStatus, isDark)
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBackground),
                    border = cardBorder,
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = if (isDark) 0.dp else 2.dp,
                            shape = RoundedCornerShape(18.dp),
                            clip = false
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left Shield / Status icon container
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(13.dp))
                                .background(badgeConfig.leftIconBg)
                        ) {
                            Icon(
                                imageVector = badgeConfig.leftIcon,
                                contentDescription = null,
                                tint = badgeConfig.tintColor,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Middle status text labels
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = badgeConfig.title,
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary
                            )
                            Text(
                                text = badgeConfig.subtitle,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = textSecondary,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Right Status Badge
                        Box(
                            modifier = Modifier
                                .background(
                                    color = badgeConfig.badgeBg,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 9.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = badgeConfig.badgeText,
                                color = badgeConfig.tintColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 3. Menu Items List Container Card
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBackground),
                    border = cardBorder,
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = if (isDark) 0.dp else 2.dp,
                            shape = RoundedCornerShape(18.dp),
                            clip = false
                        )
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        val items = listOf(
                            MenuItemData(
                                label = "Ödeme yöntemleri",
                                icon = Icons.Rounded.Payment,
                                onClick = { Toast.makeText(context, "Ödeme yöntemleri yakında aktif edilecektir.", Toast.LENGTH_SHORT).show() }
                            ),
                            MenuItemData(
                                label = "Ayarlar",
                                icon = Icons.Rounded.Settings,
                                onClick = { Toast.makeText(context, "Ayarlar yakında aktif edilecektir.", Toast.LENGTH_SHORT).show() }
                            ),
                            MenuItemData(
                                label = "Yardım & destek",
                                icon = Icons.Rounded.HelpOutline,
                                onClick = { Toast.makeText(context, "Yardım ve destek yakında aktif edilecektir.", Toast.LENGTH_SHORT).show() }
                            ),
                            MenuItemData(
                                label = "Davet et · ₺50 kazan",
                                icon = Icons.Rounded.CardGiftcard,
                                onClick = { Toast.makeText(context, "Davet kodunuz kopyalandı! ₺50 kazanmak için arkadaşlarınızla paylaşın.", Toast.LENGTH_SHORT).show() }
                            )
                        )

                        items.forEachIndexed { index, item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { item.onClick() }
                                    .padding(vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = null,
                                    tint = if (isDark) Color(0xFFB6BFCB) else Color(0xFF3A4452),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(13.dp))
                                Text(
                                    text = item.label,
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = textPrimary,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                                    contentDescription = null,
                                    tint = if (isDark) Color(0xFF4A535F) else Color(0xFFC7CFDA),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            
                            if (index < items.lastIndex) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(dividerColor)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 4. Logout Card Button
                val logoutTextColor = if (isDark) Color(0xFFF0575B) else Color(0xFFE5484D)
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBackground),
                    border = cardBorder,
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = if (isDark) 0.dp else 2.dp,
                            shape = RoundedCornerShape(16.dp),
                            clip = false
                        )
                        .clickable {
                            viewModel.onEvent(ProfileEvent.LogoutClicked)
                            onLogoutClick()
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ExitToApp,
                            contentDescription = "Log out",
                            tint = logoutTextColor,
                            modifier = Modifier.size(19.dp)
                        )
                        Spacer(modifier = Modifier.width(9.dp))
                        Text(
                            text = "Çıkış yap",
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = logoutTextColor,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

private fun getInitials(fullName: String): String {
    val parts = fullName.trim().split("\\s+".toRegex())
    if (parts.isEmpty() || parts[0].isBlank()) return "U"
    if (parts.size == 1) return parts[0].take(2).uppercase()
    return (parts[0].take(1) + parts.last().take(1)).uppercase()
}

private data class MenuItemData(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

private data class LicenseBadgeConfig(
    val title: String,
    val subtitle: String,
    val badgeText: String,
    val badgeBg: Color,
    val leftIconBg: Color,
    val tintColor: Color,
    val leftIcon: ImageVector
)

private fun getLicenseBadgeConfig(status: String, isDark: Boolean): LicenseBadgeConfig {
    return when (status) {
        "APPROVED" -> LicenseBadgeConfig(
            title = "Ehliyet doğrulandı",
            subtitle = "B sınıfı · geçerli",
            badgeText = "Onaylı",
            badgeBg = if (isDark) Color(0xFF173726) else Color(0xFFE7F4EC),
            leftIconBg = if (isDark) Color(0xFF152C20) else Color(0xFFE7F4EC),
            tintColor = if (isDark) Color(0xFF34C98A) else Color(0xFF1A9E63),
            leftIcon = Icons.Rounded.Shield
        )
        "UNDER_REVIEW" -> LicenseBadgeConfig(
            title = "Ehliyet incelemede",
            subtitle = "Belgeleriniz kontrol ediliyor",
            badgeText = "İncelemede",
            badgeBg = if (isDark) Color(0xFF3A2E15) else Color(0xFFFEF3C7),
            leftIconBg = if (isDark) Color(0xFF2E2413) else Color(0xFFFEF3C7),
            tintColor = if (isDark) Color(0xFFF59E0B) else Color(0xFFD97706),
            leftIcon = Icons.Rounded.AccessTime
        )
        "REJECTED" -> LicenseBadgeConfig(
            title = "Ehliyet onaylanmadı",
            subtitle = "Lütfen bilgilerinizi kontrol edin",
            badgeText = "Reddedildi",
            badgeBg = if (isDark) Color(0xFF3C1F21) else Color(0xFFFEE2E2),
            leftIconBg = if (isDark) Color(0xFF2E1A1B) else Color(0xFFFEE2E2),
            tintColor = if (isDark) Color(0xFFF0575B) else Color(0xFFDC3545),
            leftIcon = Icons.Rounded.ErrorOutline
        )
        else -> LicenseBadgeConfig(
            title = "Ehliyet yüklenmedi",
            subtitle = "Kiralamak için ehliyet ekleyin",
            badgeText = "Eksik",
            badgeBg = if (isDark) Color(0xFF2A2E35) else Color(0xFFF0F2F6),
            leftIconBg = if (isDark) Color(0xFF20242B) else Color(0xFFF0F2F6),
            tintColor = if (isDark) Color(0xFF7A828F) else Color(0xFF6C757D),
            leftIcon = Icons.Rounded.Info
        )
    }
}
