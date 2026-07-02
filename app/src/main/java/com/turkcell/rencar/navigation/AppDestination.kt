package com.turkcell.rencar.navigation

sealed class AppDestination(val route: String, val title: String, val showBottomBar: Boolean = false) {
    data object Splash : AppDestination("splash", "Splash")
    data object Login : AppDestination("login", "Giris Yap")
    data object Register : AppDestination("register", "Kayit Ol")
    data object Otp : AppDestination("otp", "OTP Dogrulama")
    data object License : AppDestination("license", "Ehliyet Dogrulama")
    data object Map : AppDestination("map", "Ana Harita", true)
    data object VehicleDetail : AppDestination("vehicle_detail", "Arac Detay")
    data object Reservation : AppDestination("reservation", "Rezervasyon Onayi")
    data object DeliveryPhotos : AppDestination("delivery_photos", "Arac Teslim Fotografi")
    data object ActiveRental : AppDestination("active_rental", "Aktif Kiralama")
    data object PaymentSummary : AppDestination("payment_summary", "Odeme / Kiralama Ozeti")
    data object Wallet : AppDestination("wallet", "Cuzdan", true)
    data object History : AppDestination("history", "Kiralama Gecmisi", true)
    data object Profile : AppDestination("profile", "Profil", true)

    companion object {
        val authFlow = listOf(Splash, Login, Register, Otp, License)
        val rentalFlow = listOf(Map, VehicleDetail, Reservation, DeliveryPhotos, ActiveRental, PaymentSummary)
        val mainTabs = listOf(Map, Wallet, History, Profile)
        val all = authFlow + rentalFlow + mainTabs
    }
}
