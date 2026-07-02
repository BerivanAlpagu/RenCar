package com.turkcell.rencar.ui.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.turkcell.rencar.ui.theme.PlusJakartaSans
import com.turkcell.rencar.ui.theme.RenCarIcons
import com.turkcell.rencar.ui.theme.Sora

data class OnboardingPageData(
    val title: String,
    val description: String
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onStartClick: () -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = isSystemInDarkTheme()
) {
    // Theme styling matching the design spec
    val backgroundColor = if (isDarkTheme) Color(0xFF0C0F14) else Color(0xFFEEF1F6)
    val textPrimaryColor = if (isDarkTheme) Color(0xFFF3F6FA) else Color(0xFF101620)
    val textSecondaryColor = if (isDarkTheme) Color(0xFF98A2B0) else Color(0xFF5C6675)
    val accentColor = if (isDarkTheme) Color(0xFF4C95F0) else Color(0xFF0B6BCB)
    val dotInactiveColor = if (isDarkTheme) Color(0xFF2E3742) else Color(0xFFC7CFDA)
    val radialGlowColor = if (isDarkTheme) Color(0x384C95F0) else Color(0x2E0B6BCB)

    // Logo gradient colors
    val logoGradient = if (isDarkTheme) {
        listOf(Color(0xFF3B8EF0), Color(0xFF0B6BCB))
    } else {
        listOf(Color(0xFF1E7FE0), Color(0xFF0B6BCB))
    }

    val pages = listOf(
        OnboardingPageData(
            title = "Rencar",
            description = "Yakındaki aracı bul,\ndakikalar içinde yola çık."
        ),
        OnboardingPageData(
            title = "Kolay Kiralama",
            description = "Sana en yakın aracı seç,\nsaniyeler içinde anahtarsız kilidi aç."
        ),
        OnboardingPageData(
            title = "Yolculuğa Başla",
            description = "Dilediğin kadar sür, istediğin\ngüvenli noktada kolayca teslim et."
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // Top-center radial gradient glow
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 80.dp)
                .size(320.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(radialGlowColor, Color.Transparent)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Main pager area
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                val pageData = pages[page]
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 36.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Logo box (only on first page, or modified brand elements on others)
                    Box(
                        modifier = Modifier
                            .size(98.dp)
                            .shadow(
                                elevation = 20.dp,
                                shape = RoundedCornerShape(30.dp),
                                spotColor = Color(0xFF0B6BCB),
                                ambientColor = Color(0xFF0B6BCB)
                            )
                            .background(
                                brush = Brush.linearGradient(logoGradient),
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

                    // Title
                    Text(
                        text = pageData.title,
                        fontFamily = Sora,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 38.sp,
                        color = textPrimaryColor,
                        letterSpacing = (-1.5).sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Tagline / Description
                    Text(
                        text = pageData.description,
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.5.sp,
                        color = textSecondaryColor,
                        lineHeight = 24.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Bottom actions area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp)
                    .padding(bottom = 36.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Dot indicators
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    repeat(pages.size) { index ->
                        val isActive = pagerState.currentPage == index
                        val width = if (isActive) 22.dp else 7.dp
                        val color = if (isActive) accentColor else dotInactiveColor
                        
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 3.5.dp)
                                .size(width = width, height = 7.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(color)
                        )
                    }
                }

                // Hemen Başla button
                Button(
                    onClick = onStartClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(
                            elevation = 14.dp,
                            shape = RoundedCornerShape(18.dp),
                            spotColor = Color(0xFF0B6BCB)
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

                // Footer navigation to Login
                Row(
                    modifier = Modifier.clickable { onLoginClick() }
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
                        color = accentColor
                    )
                }
            }
        }
    }
}
