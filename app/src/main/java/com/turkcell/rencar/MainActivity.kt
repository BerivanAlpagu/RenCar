package com.turkcell.rencar

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.turkcell.rencar.feature.vehicles.presentation.map.MapScreen
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.turkcell.rencar.feature.auth.presentation.otp.OtpVerificationScreen
import com.turkcell.rencar.feature.auth.presentation.register.RegisterScreen
import com.turkcell.rencar.feature.auth.presentation.onboarding.OnboardingScreen
import com.turkcell.rencar.feature.auth.presentation.splash.SplashViewModel
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
            }
        }
    }
}

@Composable
fun HomeScreen(onLogoutClick: () -> Unit) {
    var selectedItem by remember { mutableStateOf(0) }
    val items = listOf("Harita", "Geçmiş", "Cüzdan", "Profil")
    val icons = listOf(
        Icons.Filled.LocationOn,
        Icons.Filled.List,
        Icons.Filled.ShoppingCart,
        Icons.Filled.AccountCircle
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(icons[index], contentDescription = item) },
                        label = { Text(item) },
                        selected = selectedItem == index,
                        onClick = { selectedItem = index }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (selectedItem) {
                0 -> MapScreen()
                1 -> DummyPlaceholderScreen("Geçmiş Ekranı")
                2 -> DummyPlaceholderScreen("Cüzdan Ekranı")
                3 -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        DummyPlaceholderScreen("Profil Ekranı")
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onLogoutClick) {
                            Text("Çıkış Yap")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DummyPlaceholderScreen(name: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}