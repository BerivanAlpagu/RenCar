package com.turkcell.rencar.ui

import androidx.compose.runtime.Immutable

@Immutable
data class AppUiState(
    val selectedVehicleId: String = "vehicle-1",
    val selectedPlan: RentalPlan = RentalPlan.Minute,
    val activeScreen: String = "splash",
    val authMode: AuthMode = AuthMode.Login,
    val loginEmail: String = "admin@rencar.com",
    val loginPassword: String = "Admin123!",
    val registerFullName: String = "Ahmet Yilmaz",
    val registerEmail: String = "ahmet.yilmaz@example.com",
    val registerPhone: String = "+905551112233",
    val registerPassword: String = "Sifre123!",
    val vehicles: List<VehicleUiModel> = DemoContent.vehicles,
    val vehicle: VehicleUiModel = DemoContent.vehicles.first(),
    val history: List<RentalHistoryUiModel> = DemoContent.history,
    val wallet: WalletUiModel = DemoContent.wallet,
    val profile: ProfileUiModel = DemoContent.profile,
    val activeRental: ActiveRentalUiModel = DemoContent.activeRental,
    val licenseStatus: LicenseStatusUiModel = DemoContent.licenseStatus,
    val otpCode: String = "4821",
    val loading: Boolean = false
)

enum class AuthMode {
    Login,
    Register
}

enum class RentalPlan(val label: String, val price: String) {
    Minute("Dakikalik", "₺4,50/dk"),
    Hourly("Saatlik", "₺180/sa"),
    Daily("Gunluk", "₺1.450")
}

@Immutable
data class VehicleUiModel(
    val id: String,
    val brand: String,
    val model: String,
    val plate: String,
    val distance: String,
    val status: String,
    val fuelPercent: Int,
    val rangeKm: Int,
    val seats: Int,
    val transmission: String,
    val pricePerMinute: String,
    val hourlyPrice: String
)

@Immutable
data class RentalHistoryUiModel(
    val id: String,
    val title: String,
    val date: String,
    val duration: String,
    val distance: String,
    val price: String
)

@Immutable
data class WalletUiModel(
    val balance: String,
    val cards: List<String>,
    val transactions: List<String>
)

@Immutable
data class ProfileUiModel(
    val name: String,
    val phone: String,
    val licenseTitle: String,
    val licenseDetail: String
)

@Immutable
data class ActiveRentalUiModel(
    val elapsedTime: String,
    val distance: String,
    val currentFee: String,
    val vehicleLabel: String
)

@Immutable
data class LicenseStatusUiModel(
    val title: String,
    val subtitle: String,
    val frontStatus: String,
    val backStatus: String,
    val canContinue: Boolean
)

object DemoContent {
    val vehicles = listOf(
        VehicleUiModel("vehicle-1", "Renault", "Clio", "34 RNC 022", "250 m", "Müsait", 72, 480, 5, "Manuel", "₺4,50/dk", "₺180/sa"),
        VehicleUiModel("vehicle-2", "Fiat", "Egea", "34 FEA 118", "380 m", "Müsait", 64, 410, 5, "Otomatik", "₺4,20/dk", "₺160/sa"),
        VehicleUiModel("vehicle-3", "Volkswagen", "Polo", "34 VWP 079", "540 m", "Rezerve", 83, 520, 5, "Otomatik", "₺4,95/dk", "₺195/sa")
    )

    val history = listOf(
        RentalHistoryUiModel("1", "Renault Clio", "26 Haz 2026 · 14:32", "24 dk", "12,4 km", "₺110,50"),
        RentalHistoryUiModel("2", "Fiat Egea", "24 Haz 2026 · 18:05", "18 dk", "8,1 km", "₺86,00"),
        RentalHistoryUiModel("3", "Volkswagen Polo", "21 Haz 2026 · 09:48", "31 dk", "19,6 km", "₺142,00")
    )

    val wallet = WalletUiModel(
        balance = "₺340,00",
        cards = listOf("Visa •••• 4291", "Mastercard •••• 7740"),
        transactions = listOf("Renault Clio kiralama -₺110,50", "Bakiye yukleme +₺200,00")
    )

    val profile = ProfileUiModel(
        name = "Deniz Yilmaz",
        phone = "+90 532 000 00 00",
        licenseTitle = "Ehliyet dogrulandi",
        licenseDetail = "B sinifi · gecerli"
    )

    val activeRental = ActiveRentalUiModel(
        elapsedTime = "00:24:18",
        distance = "12,4 km",
        currentFee = "₺108,00",
        vehicleLabel = "Kiralama aktif · Renault Clio"
    )

    val licenseStatus = LicenseStatusUiModel(
        title = "Ehliyet dogrulama",
        subtitle = "Kiralamadan once tek seferlik",
        frontStatus = "Yuklendi",
        backStatus = "Bekliyor",
        canContinue = true
    )
}
