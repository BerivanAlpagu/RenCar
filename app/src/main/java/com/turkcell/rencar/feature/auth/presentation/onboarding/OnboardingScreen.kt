package com.turkcell.rencar.feature.auth.presentation.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.turkcell.rencar.core.designsystem.PlusJakartaSans
import com.turkcell.rencar.core.designsystem.RenCarIcons
import com.turkcell.rencar.core.designsystem.Sora

@Composable
fun OnboardingScreen(
    onStartClick: () -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = isSystemInDarkTheme()
) {
    val backgroundColor = if (isDarkTheme) Color(0xFF0C0F14) else Color(0xFFEEF1F6)
    val textPrimaryColor = if (isDarkTheme) Color(0xFFF3F6FA) else Color(0xFF101620)
    val textSecondaryColor = if (isDarkTheme) Color(0xFF98A2B0) else Color(0xFF5C6675)
    val primaryAccentColor = if (isDarkTheme) Color(0xFF4C95F0) else Color(0xFF0B6BCB)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        if (!isDarkTheme) {
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .align(Alignment.TopCenter)
                    .offset(y = 96.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0x2E0B6BCB), // 18% alpha approx
                                Color(0x000B6BCB)
                            ),
                            radius = 400f
                        )
                    )
            )
        } else {
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .align(Alignment.TopCenter)
                    .offset(y = 96.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0x384C95F0), // 22% alpha approx
                                Color(0x004C95F0)
                            ),
                            radius = 400f
                        )
                    )
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .size(98.dp)
                    .shadow(
                        elevation = if (isDarkTheme) 20.dp else 20.dp,
                        shape = RoundedCornerShape(30.dp),
                        spotColor = if (isDarkTheme) Color(0x800B6BCB) else Color(0x660B6BCB)
                    )
                    .background(
                        brush = Brush.linearGradient(
                            colors = if (isDarkTheme) {
                                listOf(Color(0xFF3B8EF0), Color(0xFF0B6BCB))
                            } else {
                                listOf(Color(0xFF1E7FE0), Color(0xFF0B6BCB))
                            }
                        ),
                        shape = RoundedCornerShape(30.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = RenCarIcons.CarLogo,
                    contentDescription = "Rencar Logo",
                    tint = Color.White,
                    modifier = Modifier.size(50.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Rencar",
                fontFamily = Sora,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 38.sp,
                color = textPrimaryColor,
                letterSpacing = (-1.5).sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Yakındaki aracı bul,\ndakikalar içinde yola çık.",
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.Medium,
                fontSize = 15.5.sp,
                lineHeight = 24.sp,
                textAlign = TextAlign.Center,
                color = textSecondaryColor
            )
            
            Spacer(modifier = Modifier.weight(1f))

            // Indicator dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .height(7.dp)
                        .width(22.dp)
                        .background(primaryAccentColor, RoundedCornerShape(4.dp))
                )
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .background(if (isDarkTheme) Color(0xFF2E3742) else Color(0xFFC7CFDA), RoundedCornerShape(4.dp))
                )
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .background(if (isDarkTheme) Color(0xFF2E3742) else Color(0xFFC7CFDA), RoundedCornerShape(4.dp))
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onStartClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp)
                    .height(56.dp)
                    .shadow(
                        elevation = 14.dp,
                        shape = RoundedCornerShape(18.dp),
                        spotColor = if (isDarkTheme) Color(0x730B6BCB) else Color(0x570B6BCB)
                    ),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0B6BCB),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Hemen Başla",
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.5.sp
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 36.dp)
                    .clickable { onLoginClick() },
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Zaten hesabım var · ",
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.5.sp,
                    color = textSecondaryColor
                )
                Text(
                    text = "Giriş yap",
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.5.sp,
                    color = primaryAccentColor
                )
            }
        }
        
        // Bottom subtle line indicator
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 9.dp)
                .width(128.dp)
                .height(5.dp)
                .background(
                    if (isDarkTheme) Color(0x3DEAEEF3) else Color(0x33141A22),
                    RoundedCornerShape(3.dp)
                )
        )
    }
}
