package com.turkcell.rencar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
<<<<<<< Updated upstream
import androidx.compose.foundation.layout.*
=======
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
>>>>>>> Stashed changes
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.turkcell.rencar.feature.rentals.presentation.history.RentalHistoryScreen
import com.turkcell.rencar.feature.wallet.presentation.wallet.WalletScreen
import com.turkcell.rencar.ui.theme.RenCarTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RenCarTheme {
<<<<<<< Updated upstream
                MainAppShell()
=======
                val navController = rememberNavController()
                val splashState by splashViewModel.state.collectAsState()
                val context = LocalContext.current

                LaunchedEffect(Unit) {
                    authViewModel.events.collect { event ->
                        when (event) {
                            is AuthViewModel.AuthEvent.NavigateToOtp -> {
                                navController.navigate(Screen.Otp(event.phone))
                            }
                            is AuthViewModel.AuthEvent.NavigateToHome -> {
                                navController.navigate(Screen.Home) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                            is AuthViewModel.AuthEvent.NavigateToOnboarding -> {
                                navController.navigate(Screen.Onboarding) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                            is AuthViewModel.AuthEvent.ShowError -> {
                                Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Splash,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        composable<Screen.Splash> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.background),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = Color(0xFF0B6BCB))
                            }

                            LaunchedEffect(splashState) {
                                when (splashState) {
                                    SplashViewModel.SplashState.NavigateToOnboarding -> {
                                        navController.navigate(Screen.Onboarding) {
                                            popUpTo(Screen.Splash) { inclusive = true }
                                        }
                                    }
                                    SplashViewModel.SplashState.NavigateToHome -> {
                                        navController.navigate(Screen.Home) {
                                            popUpTo(Screen.Splash) { inclusive = true }
                                        }
                                    }
                                    SplashViewModel.SplashState.Loading -> {}
                                }
                            }
                        }

                        composable<Screen.Onboarding> {
                            OnboardingScreen(
                                onStartClick = {
                                    navController.navigate(Screen.Register)
                                },
                                onLoginClick = {
                                    navController.navigate(Screen.Login)
                                }
                            )
                        }

                        composable<Screen.Login> {
                            LoginScreen(
                                viewModel = authViewModel,
                                onBackClick = {
                                    navController.navigateUp()
                                },
                                onRegisterClick = {
                                    navController.navigate(Screen.Register) {
                                        popUpTo(Screen.Login) { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable<Screen.Register> {
                            RegisterScreen(
                                viewModel = authViewModel,
                                onBackClick = {
                                    navController.navigateUp()
                                },
                                onLoginClick = {
                                    navController.navigate(Screen.Login) {
                                        popUpTo(Screen.Register) { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable<Screen.Otp> { backStackEntry ->
                            val route = backStackEntry.toRoute<Screen.Otp>()
                            OtpVerificationScreen(
                                phone = route.phone,
                                viewModel = authViewModel,
                                onBackClick = {
                                    navController.navigateUp()
                                },
                                onChangePhoneClick = {
                                    navController.navigate(Screen.Login) {
                                        popUpTo(Screen.Otp::class) { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable<Screen.Home> {
                            HomeScreen(
                                onLogoutClick = { authViewModel.logout() }
                            )
                        }
                    }
                }
>>>>>>> Stashed changes
            }
        }
    }
}

@Composable
fun MainAppShell() {
    var selectedTab by remember { mutableStateOf("Geçmiş") }
    val isDark = isSystemInDarkTheme()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            CustomBottomBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                isDark = isDark
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                "Geçmiş" -> {
                    RentalHistoryScreen()
                }
                "Cüzdan" -> {
                    WalletScreen()
                }
                else -> {
                    // Placeholder screens for other tabs
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(if (isDark) Color(0xFF0C0F14) else Color(0xFFF4F6F9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$selectedTab Ekranı",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color(0xFFF3F6FA) else Color(0xFF101620)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CustomBottomBar(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    isDark: Boolean
) {
    val barBackground = if (isDark) Color(0xFF10151B) else Color(0xFFFFFFFF)
    val shadowColor = if (isDark) Color.Transparent else Color(0x10101828)
    val density = LocalDensity.current.density
    
    val borderModifier = if (isDark) {
        Modifier.background(barBackground)
    } else {
        Modifier
            .shadow(elevation = 8.dp)
            .background(barBackground)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(borderModifier)
    ) {
        // Top line for dark mode
        if (isDark) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFF1E252E))
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomBarItem(
                name = "Harita",
                isSelected = selectedTab == "Harita",
                isDark = isDark,
                onClick = { onTabSelected("Harita") },
                drawIcon = { color ->
                    Canvas(modifier = Modifier.size(22.dp)) {
                        // Map Pin path: d="M12 21s7-6.3 7-11a7 7 0 1 0-14 0c0 4.7 7 11 7 11Z"
                        val scaleX = size.width / 24f
                        val scaleY = size.height / 24f
                        val path = Path().apply {
                            moveTo(12f * scaleX, 21f * scaleY)
                            cubicTo(
                                12f * scaleX, 21f * scaleY,
                                19f * scaleX, 14.7f * scaleY,
                                19f * scaleX, 10f * scaleY
                            )
                            cubicTo(
                                19f * scaleX, 6.13f * scaleY,
                                15.87f * scaleX, 3f * scaleY,
                                12f * scaleX, 3f * scaleY
                            )
                            cubicTo(
                                8.13f * scaleX, 3f * scaleY,
                                5f * scaleX, 6.13f * scaleY,
                                5f * scaleX, 10f * scaleY
                            )
                            cubicTo(
                                5f * scaleX, 14.7f * scaleY,
                                12f * scaleX, 21f * scaleY,
                                12f * scaleX, 21f * scaleY
                            )
                        }
                        drawPath(
                            path = path,
                            color = color,
                            style = Stroke(width = 1.8f * density, cap = StrokeCap.Round)
                        )
                        // Circle in middle
                        drawCircle(
                            color = color,
                            radius = 2.4f * density,
                            center = Offset(12f * scaleX, 10f * scaleY),
                            style = Stroke(width = 1.8f * density)
                        )
                    }
                }
            )

            BottomBarItem(
                name = "Geçmiş",
                isSelected = selectedTab == "Geçmiş",
                isDark = isDark,
                onClick = { onTabSelected("Geçmiş") },
                drawIcon = { color ->
                    Canvas(modifier = Modifier.size(22.dp)) {
                        val scaleX = size.width / 24f
                        val scaleY = size.height / 24f
                        
                        // L-shape clock hands inside: d="M12 8v4l3 2"
                        val handPath = Path().apply {
                            moveTo(12f * scaleX, 8f * scaleY)
                            lineTo(12f * scaleX, 12f * scaleY)
                            lineTo(15f * scaleX, 14f * scaleY)
                        }
                        drawPath(
                            path = handPath,
                            color = color,
                            style = Stroke(width = 2f * density, cap = StrokeCap.Round)
                        )
                        
                        // Counter-clockwise circle path: d="M21 12a9 9 0 1 1-9-9"
                        val circlePath = Path().apply {
                            moveTo(21f * scaleX, 12f * scaleY)
                            // Arc representing circle: from 0 deg (3 o'clock) counter-clockwise
                            arcTo(
                                rect = Rect(
                                    left = 3f * scaleX,
                                    top = 3f * scaleY,
                                    right = 21f * scaleX,
                                    bottom = 21f * scaleY
                                ),
                                startAngleDegrees = 0f,
                                sweepAngleDegrees = 359f,
                                forceMoveTo = false
                            )
                        }
                        drawPath(
                            path = circlePath,
                            color = color,
                            style = Stroke(width = 2f * density, cap = StrokeCap.Round)
                        )
                        
                        // Clock arrow tip: d="M21 4v4h-4"
                        val arrowPath = Path().apply {
                            moveTo(21f * scaleX, 4f * scaleY)
                            lineTo(21f * scaleX, 8f * scaleY)
                            lineTo(17f * scaleX, 8f * scaleY)
                        }
                        drawPath(
                            path = arrowPath,
                            color = color,
                            style = Stroke(width = 2f * density, cap = StrokeCap.Round, join = StrokeJoin.Round)
                        )
                    }
                }
            )

            BottomBarItem(
                name = "Cüzdan",
                isSelected = selectedTab == "Cüzdan",
                isDark = isDark,
                onClick = { onTabSelected("Cüzdan") },
                drawIcon = { color ->
                    Canvas(modifier = Modifier.size(22.dp)) {
                        val scaleX = size.width / 24f
                        val scaleY = size.height / 24f
                        
                        // Wallet outer rectangle: height="13" rx="3" width="18" x="3" y="6"
                        val rectPath = Path().apply {
                            addRoundRect(
                                RoundRect(
                                    rect = Rect(
                                        left = 3f * scaleX,
                                        top = 6f * scaleY,
                                        right = 21f * scaleX,
                                        bottom = 19f * scaleY
                                    ),
                                    cornerRadiusX = 3f * scaleX,
                                    cornerRadiusY = 3f * scaleY
                                )
                            )
                        }
                        drawPath(
                            path = rectPath,
                            color = color,
                            style = Stroke(width = 1.8f * density)
                        )
                        
                        // Line: d="M3 10h18M16 14h2"
                        drawLine(
                            color = color,
                            start = Offset(3f * scaleX, 10f * scaleY),
                            end = Offset(21f * scaleX, 10f * scaleY),
                            strokeWidth = 1.8f * density,
                            cap = StrokeCap.Round
                        )
                        drawLine(
                            color = color,
                            start = Offset(16f * scaleX, 14f * scaleY),
                            end = Offset(18f * scaleX, 14f * scaleY),
                            strokeWidth = 1.8f * density,
                            cap = StrokeCap.Round
                        )
                    }
                }
            )

            BottomBarItem(
                name = "Profil",
                isSelected = selectedTab == "Profil",
                isDark = isDark,
                onClick = { onTabSelected("Profil") },
                drawIcon = { color ->
                    Canvas(modifier = Modifier.size(22.dp)) {
                        val scaleX = size.width / 24f
                        val scaleY = size.height / 24f
                        
                        // Head circle: cx="12" cy="8" r="3.4"
                        drawCircle(
                            color = color,
                            radius = 3.4f * scaleX * density,
                            center = Offset(12f * scaleX, 8f * scaleY),
                            style = Stroke(width = 1.8f * density)
                        )
                        
                        // Body shoulder arc: d="M5 20a7 7 0 0 1 14 0"
                        val shoulderPath = Path().apply {
                            moveTo(5f * scaleX, 20f * scaleY)
                            arcTo(
                                rect = Rect(
                                    left = 5f * scaleX,
                                    top = 13f * scaleY,
                                    right = 19f * scaleX,
                                    bottom = 27f * scaleY
                                ),
                                startAngleDegrees = 180f,
                                sweepAngleDegrees = 180f,
                                forceMoveTo = false
                            )
                        }
                        drawPath(
                            path = shoulderPath,
                            color = color,
                            style = Stroke(width = 1.8f * density, cap = StrokeCap.Round)
                        )
                    }
                }
            )
        }

        // Bottom Home Indicator Bar (Decorative)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 9.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(128.dp)
                    .height(5.dp)
                    .background(
                        color = if (isDark) Color(0x3DDFE4EE) else Color(0x2E141A22),
                        shape = RoundedCornerShape(3.dp)
                    )
            )
        }
    }
}

@Composable
fun BottomBarItem(
    name: String,
    isSelected: Boolean,
    isDark: Boolean,
    onClick: () -> Unit,
    drawIcon: @Composable (Color) -> Unit
) {
    val activeColor = if (isDark) Color(0xFF4C95F0) else Color(0xFF0B6BCB)
    val inactiveColor = if (isDark) Color(0xFF6B7480) else Color(0xFF9AA3AE)
    val textColor = if (isSelected) activeColor else inactiveColor
    val fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold

    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        drawIcon(textColor)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = name,
            color = textColor,
            fontSize = 10.5.sp,
            fontWeight = fontWeight
        )
    }
}