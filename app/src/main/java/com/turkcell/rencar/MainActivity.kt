package com.turkcell.rencar

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.turkcell.rencar.app.navigation.Screen
import com.turkcell.rencar.core.designsystem.RenCarTheme
import com.turkcell.rencar.feature.auth.presentation.AuthViewModel
import com.turkcell.rencar.feature.auth.presentation.login.LoginScreen
import com.turkcell.rencar.feature.auth.presentation.license.LicenseApprovalScreen
import com.turkcell.rencar.feature.auth.presentation.license.LicenseUploadScreen
import com.turkcell.rencar.feature.auth.presentation.license.LicenseViewModel
import com.turkcell.rencar.feature.auth.presentation.onboarding.OnboardingScreen
import com.turkcell.rencar.feature.auth.presentation.otp.OtpVerificationScreen
import com.turkcell.rencar.feature.auth.presentation.register.RegisterScreen
import com.turkcell.rencar.feature.auth.presentation.splash.SplashViewModel
import com.turkcell.rencar.feature.rentals.presentation.history.RentalHistoryScreen
import com.turkcell.rencar.feature.vehicles.presentation.map.MapScreen
import com.turkcell.rencar.feature.wallet.presentation.wallet.WalletScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val splashViewModel: SplashViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        org.maplibre.android.MapLibre.getInstance(this)
        enableEdgeToEdge()
        setContent {
            RenCarTheme {
                val navController = rememberNavController()
                val splashState by splashViewModel.state.collectAsState()
                val context = LocalContext.current

                LaunchedEffect(Unit) {
                    authViewModel.events.collect { event ->
                        when (event) {
                            is AuthViewModel.AuthEvent.NavigateToOtp -> {
                                navController.navigate(Screen.Otp(event.phone))
                            }
                            is AuthViewModel.AuthEvent.NavigateToLicense -> {
                                navController.navigate(Screen.License)
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

                NavHost(
                    navController = navController,
                    startDestination = Screen.Splash,
                    modifier = Modifier.fillMaxSize()
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
                                SplashViewModel.SplashState.NavigateToLicense -> {
                                    navController.navigate(Screen.License) {
                                        popUpTo(Screen.Splash) { inclusive = true }
                                    }
                                }
                                SplashViewModel.SplashState.NavigateToLicenseApproval -> {
                                    navController.navigate(Screen.LicenseApproval) {
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
                            onStartClick = { navController.navigate(Screen.Register) },
                            onLoginClick = { navController.navigate(Screen.Login) }
                        )
                    }

                    composable<Screen.Login> {
                        LoginScreen(
                            viewModel = authViewModel,
                            onBackClick = { navController.navigateUp() },
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
                            onBackClick = { navController.navigateUp() },
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
                            onBackClick = { navController.navigateUp() },
                            onChangePhoneClick = {
                                navController.navigate(Screen.Login) {
                                    popUpTo(Screen.Otp::class) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable<Screen.License> {
                        val licenseViewModel: LicenseViewModel = androidx.hilt.navigation.compose.hiltViewModel()
                        LicenseUploadScreen(
                            viewModel = licenseViewModel,
                            onGoToApproval = {
                                navController.navigate(Screen.LicenseApproval) {
                                    popUpTo(Screen.License) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable<Screen.LicenseApproval> {
                        val licenseViewModel: LicenseViewModel = androidx.hilt.navigation.compose.hiltViewModel()
                        LicenseApprovalScreen(
                            viewModel = licenseViewModel,
                            onApproved = {
                                navController.navigate(Screen.Home) {
                                    popUpTo(Screen.LicenseApproval) { inclusive = true }
                                }
                            },
                            onRejected = {
                                navController.navigate(Screen.License) {
                                    popUpTo(Screen.LicenseApproval) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable<Screen.Home> {
                        HomeScreen(onLogoutClick = { authViewModel.logout() }, navController = navController)
                    }

                    composable<Screen.ReservationConfirmation> { backStackEntry ->
                        val route = backStackEntry.toRoute<Screen.ReservationConfirmation>()
                        com.turkcell.rencar.feature.rentals.presentation.reservation.ReservationConfirmationScreen(
                            vehicleId = route.vehicleId,
                            onBackClick = { navController.navigateUp() },
                            onConfirmClick = { vehicleId ->
                                navController.navigate(Screen.HandoverPhoto(vehicleId)) {
                                    popUpTo(Screen.ReservationConfirmation::class) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable<Screen.HandoverPhoto> { backStackEntry ->
                        val route = backStackEntry.toRoute<Screen.HandoverPhoto>()
                        com.turkcell.rencar.feature.rentals.presentation.handover.HandoverPhotoScreen(
                            vehicleId = route.vehicleId,
                            onBackClick = { navController.navigateUp() },
                            onStartRentalClick = { vehicleId ->
                                navController.navigate(Screen.ActiveRental(vehicleId)) {
                                    popUpTo(Screen.Home) { inclusive = false } // Keep Home in stack, remove Handover
                                }
                            }
                        )
                    }

                    composable<Screen.ActiveRental> { backStackEntry ->
                        val route = backStackEntry.toRoute<Screen.ActiveRental>()
                        val activeRentalViewModel = androidx.hilt.navigation.compose.hiltViewModel<com.turkcell.rencar.feature.rentals.presentation.active.ActiveRentalViewModel>()
                        com.turkcell.rencar.feature.rentals.presentation.active.ActiveRentalScreen(
                            vehicleId = route.vehicleId,
                            viewModel = activeRentalViewModel,
                            onFinishRentalClick = {
                                navController.navigate(Screen.PaymentSummary(route.vehicleId))
                            }
                        )
                    }

                    composable<Screen.PaymentSummary> {
                        com.turkcell.rencar.feature.rentals.presentation.payment.PaymentSummaryScreen(
                            onPayClick = {
                                navController.navigate(Screen.Home) {
                                    popUpTo(Screen.Home) { inclusive = true }
                                }
                            },
                            onCloseClick = {
                                navController.navigate(Screen.Home) {
                                    popUpTo(Screen.Home) { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Ana uygulama iskeleti: 4 sekmeli (Harita, Geçmiş, Cüzdan, Profil) bottom bar
 * ve buna bağlı içerik alanı.
 */
@Composable
fun HomeScreen(onLogoutClick: () -> Unit, navController: androidx.navigation.NavController) {
    var selectedTab by remember { mutableStateOf("Harita") }
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
                "Harita" -> MapScreen(
                    onReserveClick = { vehicleId ->
                        navController.navigate(Screen.ReservationConfirmation(vehicleId))
                    }
                )
                "Geçmiş" -> RentalHistoryScreen()
                "Cüzdan" -> WalletScreen()
                "Profil" -> ProfileTab(onLogoutClick = onLogoutClick, isDark = isDark)
            }
        }
    }
}

@Composable
fun ProfileTab(onLogoutClick: () -> Unit, isDark: Boolean) {
    val backgroundColor = if (isDark) Color(0xFF0C0F14) else Color(0xFFEEF1F6)
    val textColor = if (isDark) Color(0xFFF3F6FA) else Color(0xFF101620)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "👤  Profil",
                style = MaterialTheme.typography.headlineMedium,
                color = textColor
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onLogoutClick,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFDC3545),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(52.dp)
            ) {
                Text(
                    text = "Çıkış Yap",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
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
                        val scaleX = size.width / 24f
                        val scaleY = size.height / 24f
                        val path = Path().apply {
                            moveTo(12f * scaleX, 21f * scaleY)
                            cubicTo(12f * scaleX, 21f * scaleY, 19f * scaleX, 14.7f * scaleY, 19f * scaleX, 10f * scaleY)
                            cubicTo(19f * scaleX, 6.13f * scaleY, 15.87f * scaleX, 3f * scaleY, 12f * scaleX, 3f * scaleY)
                            cubicTo(8.13f * scaleX, 3f * scaleY, 5f * scaleX, 6.13f * scaleY, 5f * scaleX, 10f * scaleY)
                            cubicTo(5f * scaleX, 14.7f * scaleY, 12f * scaleX, 21f * scaleY, 12f * scaleX, 21f * scaleY)
                        }
                        drawPath(path = path, color = color, style = Stroke(width = 1.8f * density, cap = StrokeCap.Round))
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

                        val handPath = Path().apply {
                            moveTo(12f * scaleX, 8f * scaleY)
                            lineTo(12f * scaleX, 12f * scaleY)
                            lineTo(15f * scaleX, 14f * scaleY)
                        }
                        drawPath(path = handPath, color = color, style = Stroke(width = 2f * density, cap = StrokeCap.Round))

                        val circlePath = Path().apply {
                            moveTo(21f * scaleX, 12f * scaleY)
                            arcTo(
                                rect = Rect(left = 3f * scaleX, top = 3f * scaleY, right = 21f * scaleX, bottom = 21f * scaleY),
                                startAngleDegrees = 0f,
                                sweepAngleDegrees = 359f,
                                forceMoveTo = false
                            )
                        }
                        drawPath(path = circlePath, color = color, style = Stroke(width = 2f * density, cap = StrokeCap.Round))

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

                        val rectPath = Path().apply {
                            addRoundRect(
                                RoundRect(
                                    left = 3f * scaleX, top = 6f * scaleY, right = 21f * scaleX, bottom = 19f * scaleY,
                                    radiusX = 3f * scaleX,
                                    radiusY = 3f * scaleY
                                )
                            )
                        }
                        drawPath(path = rectPath, color = color, style = Stroke(width = 1.8f * density))

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

                        drawCircle(
                            color = color,
                            radius = 3.4f * scaleX * density,
                            center = Offset(12f * scaleX, 8f * scaleY),
                            style = Stroke(width = 1.8f * density)
                        )

                        val shoulderPath = Path().apply {
                            moveTo(5f * scaleX, 20f * scaleY)
                            arcTo(
                                rect = Rect(left = 5f * scaleX, top = 13f * scaleY, right = 19f * scaleX, bottom = 27f * scaleY),
                                startAngleDegrees = 180f,
                                sweepAngleDegrees = 180f,
                                forceMoveTo = false
                            )
                        }
                        drawPath(path = shoulderPath, color = color, style = Stroke(width = 1.8f * density, cap = StrokeCap.Round))
                    }
                }
            )
        }

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
