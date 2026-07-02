package com.turkcell.rencar

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.turkcell.rencar.ui.auth.AuthViewModel
import com.turkcell.rencar.ui.auth.LoginScreen
import com.turkcell.rencar.ui.auth.OtpVerificationScreen
import com.turkcell.rencar.ui.auth.RegisterScreen
import com.turkcell.rencar.ui.navigation.Screen
import com.turkcell.rencar.ui.onboarding.OnboardingScreen
import com.turkcell.rencar.ui.splash.SplashViewModel
import com.turkcell.rencar.ui.theme.RenCarTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val splashViewModel: SplashViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RenCarTheme {
                val navController = rememberNavController()
                val splashState by splashViewModel.state.collectAsState()
                val context = LocalContext.current

                // Listen to AuthEvents
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
                            DummyPlaceholderScreen("Home Screen (Ana Sayfa)")
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