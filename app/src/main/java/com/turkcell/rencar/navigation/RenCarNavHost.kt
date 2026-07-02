package com.turkcell.rencar.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.turkcell.rencar.ui.ActiveRentalScreen
import com.turkcell.rencar.ui.AppEvent
import com.turkcell.rencar.ui.AppUiState
import com.turkcell.rencar.ui.LoginScreen
import com.turkcell.rencar.ui.DeliveryPhotosScreen
import com.turkcell.rencar.ui.HistoryScreen
import com.turkcell.rencar.ui.LicenseScreen
import com.turkcell.rencar.ui.MapScreen
import com.turkcell.rencar.ui.OtpScreen
import com.turkcell.rencar.ui.PaymentSummaryScreen
import com.turkcell.rencar.ui.RegisterScreen
import com.turkcell.rencar.ui.ProfileScreen
import com.turkcell.rencar.ui.ReservationScreen
import com.turkcell.rencar.ui.RentalPlan
import com.turkcell.rencar.ui.SplashScreen
import com.turkcell.rencar.ui.VehicleDetailScreen
import com.turkcell.rencar.ui.WalletScreen

@Composable
fun RenCarNavHost(
    appState: AppUiState,
    onEvent: (AppEvent) -> Unit,
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: AppDestination.Splash.route

    NavHost(
        navController = navController,
        startDestination = AppDestination.Splash.route
    ) {
        composable(AppDestination.Splash.route) {
            SplashScreen(
                onStart = { navController.navigate(AppDestination.Login.route) },
                onLogin = { navController.navigate(AppDestination.Login.route) }
            )
        }

        composable(AppDestination.Login.route) {
            LoginScreen(
                email = appState.loginEmail,
                password = appState.loginPassword,
                onEmailChange = { onEvent(AppEvent.UpdateLoginEmail(it)) },
                onPasswordChange = { onEvent(AppEvent.UpdateLoginPassword(it)) },
                onLogin = { navController.navigate(AppDestination.Map.route) },
                onGoToRegister = { navController.navigate(AppDestination.Register.route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppDestination.Register.route) {
            RegisterScreen(
                fullName = appState.registerFullName,
                email = appState.registerEmail,
                phone = appState.registerPhone,
                password = appState.registerPassword,
                onFullNameChange = { onEvent(AppEvent.UpdateRegisterFullName(it)) },
                onEmailChange = { onEvent(AppEvent.UpdateRegisterEmail(it)) },
                onPhoneChange = { onEvent(AppEvent.UpdateRegisterPhone(it)) },
                onPasswordChange = { onEvent(AppEvent.UpdateRegisterPassword(it)) },
                onRegister = { navController.navigate(AppDestination.License.route) },
                onGoToLogin = { navController.navigate(AppDestination.Login.route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppDestination.Otp.route) {
            OtpScreen(
                code = appState.otpCode,
                onCodeChange = { onEvent(AppEvent.UpdateOtp(it)) },
                onContinue = { navController.navigate(AppDestination.License.route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppDestination.License.route) {
            LicenseScreen(
                status = appState.licenseStatus,
                onContinue = { navController.navigate(AppDestination.Map.route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppDestination.Map.route) {
            MapScreen(
                appState = appState,
                onVehicleSelected = {
                    onEvent(AppEvent.SelectVehicle(it.id))
                    navController.navigate(AppDestination.VehicleDetail.route)
                },
                onNavigate = { route -> navigateSingleTop(navController, route) }
            )
        }

        composable(AppDestination.VehicleDetail.route) {
            VehicleDetailScreen(
                vehicle = appState.vehicle,
                onReserve = { navController.navigate(AppDestination.Reservation.route) },
                onUnlock = { navController.navigate(AppDestination.Reservation.route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppDestination.Reservation.route) {
            ReservationScreen(
                appState = appState,
                onPlanSelected = { onEvent(AppEvent.SelectPlan(it)) },
                onComplete = { navController.navigate(AppDestination.DeliveryPhotos.route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppDestination.DeliveryPhotos.route) {
            DeliveryPhotosScreen(
                onContinue = { navController.navigate(AppDestination.ActiveRental.route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppDestination.ActiveRental.route) {
            ActiveRentalScreen(
                activeRental = appState.activeRental,
                onFinish = { navController.navigate(AppDestination.PaymentSummary.route) },
                onUnlock = { },
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppDestination.PaymentSummary.route) {
            PaymentSummaryScreen(
                onPay = { navController.navigate(AppDestination.Wallet.route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppDestination.Wallet.route) {
            WalletScreen(
                wallet = appState.wallet,
                onNavigate = { route -> navigateSingleTop(navController, route) }
            )
        }

        composable(AppDestination.History.route) {
            HistoryScreen(
                items = appState.history,
                onNavigate = { route -> navigateSingleTop(navController, route) }
            )
        }

        composable(AppDestination.Profile.route) {
            ProfileScreen(
                profile = appState.profile,
                onNavigate = { route -> navigateSingleTop(navController, route) }
            )
        }
    }

    LaunchedEffect(currentRoute) {
        onEvent(AppEvent.MarkScreen(currentRoute))
    }
}

private fun navigateSingleTop(navController: NavHostController, route: String) {
    navController.navigate(route) {
        launchSingleTop = true
        restoreState = true
        popUpTo(AppDestination.Map.route) {
            saveState = true
        }
    }
}
