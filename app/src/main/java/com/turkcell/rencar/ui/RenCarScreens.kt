package com.turkcell.rencar.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.turkcell.rencar.navigation.AppDestination
import com.turkcell.rencar.ui.theme.BrandAmber
import com.turkcell.rencar.ui.theme.BrandBlue
import com.turkcell.rencar.ui.theme.BrandMint
import com.turkcell.rencar.ui.theme.BrandRed
import com.turkcell.rencar.ui.theme.Neutral0
import com.turkcell.rencar.ui.theme.Neutral100
import com.turkcell.rencar.ui.theme.Neutral200
import com.turkcell.rencar.ui.theme.Neutral300
import com.turkcell.rencar.ui.theme.Neutral500
import com.turkcell.rencar.ui.theme.Neutral700
import com.turkcell.rencar.ui.theme.Neutral900

@Composable
fun SplashScreen(
    onStart: () -> Unit,
    onLogin: () -> Unit
) {
    ScreenBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(36.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(104.dp)
                        .background(
                            brush = Brush.linearGradient(
                                listOf(BrandBlue, BrandBlue.copy(alpha = 0.65f))
                            ),
                            shape = RoundedCornerShape(28.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("R", color = Neutral0, style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    "Rencar",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "Yakindaki araci bul, dakikalar icinde yola cik.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(3) { index ->
                        Box(
                            modifier = Modifier
                                .size(if (index == 0) 22.dp else 8.dp)
                                .background(
                                    if (index == 0) BrandBlue else Neutral200,
                                    RoundedCornerShape(99.dp)
                                )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                PrimaryButton(
                    text = "Hemen Basla",
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onStart
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Zaten hesabim var · Giris yap",
                    modifier = Modifier.clickable(onClick = onLogin),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun LoginScreen(
    email: String,
    password: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLogin: () -> Unit,
    onGoToRegister: () -> Unit,
    onBack: () -> Unit
) {
    val validation = validateLogin(email, password)

    ScreenBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Spacer(modifier = Modifier.height(28.dp))
                AuthModeToggle(
                    loginSelected = true,
                    onLogin = {},
                    onRegister = onGoToRegister
                )
                Spacer(modifier = Modifier.height(18.dp))
                ScreenCard(modifier = Modifier.fillMaxWidth(), darkTint = Color(0xFF12233F)) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .background(
                                    brush = Brush.linearGradient(listOf(BrandBlue, BrandBlue.copy(alpha = 0.65f))),
                                    shape = RoundedCornerShape(22.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("R", color = Neutral0, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Tekrar hos geldin",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Hesabina gir ve kiralamaya devam et.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
                Spacer(modifier = Modifier.height(28.dp))
                ScreenCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        MockTextField(
                            value = email,
                            onValueChange = onEmailChange,
                            label = "E-posta",
                            isError = validation.emailError != null,
                            supportingText = validation.emailError?.message ?: "Kayitli e-posta adresini gir."
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        MockTextField(
                            value = password,
                            onValueChange = onPasswordChange,
                            label = "Parola",
                            isError = validation.passwordError != null,
                            supportingText = validation.passwordError?.message ?: "En az 8 karakter kullanman tavsiye edilir."
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        PrimaryButton(
                            text = "Giris Yap",
                            modifier = Modifier.fillMaxWidth(),
                            enabled = validation.canSubmit,
                            onClick = onLogin
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        SecondaryButton(text = "Kayit Ol", modifier = Modifier.fillMaxWidth(), onClick = onGoToRegister)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                SecondaryButton(text = "Geri", modifier = Modifier.fillMaxWidth(), onClick = onBack)
            }
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                text = "Hesabin yok mu? Kayit ol",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable(onClick = onGoToRegister),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun RegisterScreen(
    fullName: String,
    email: String,
    phone: String,
    password: String,
    onFullNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRegister: () -> Unit,
    onGoToLogin: () -> Unit,
    onBack: () -> Unit
) {
    val validation = validateRegister(fullName, email, phone, password)

    ScreenBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Spacer(modifier = Modifier.height(28.dp))
                AuthModeToggle(
                    loginSelected = false,
                    onLogin = onGoToLogin,
                    onRegister = {}
                )
                Spacer(modifier = Modifier.height(18.dp))
                ScreenCard(modifier = Modifier.fillMaxWidth(), darkTint = Color(0xFF12233F)) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .background(
                                    brush = Brush.linearGradient(listOf(BrandMint, BrandBlue)),
                                    shape = RoundedCornerShape(22.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("+", color = Neutral0, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Hesap olustur",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Dakikalar icinde kaydol ve kiralamaya basla.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
                Spacer(modifier = Modifier.height(28.dp))
                ScreenCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        MockTextField(
                            value = fullName,
                            onValueChange = onFullNameChange,
                            label = "Ad Soyad",
                            isError = validation.fullNameError != null,
                            supportingText = validation.fullNameError?.message ?: "Resmi kimlikteki adini yaz."
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        MockTextField(
                            value = email,
                            onValueChange = onEmailChange,
                            label = "E-posta",
                            isError = validation.emailError != null,
                            supportingText = validation.emailError?.message ?: "Aktif kullandigin e-posta adresi."
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        MockTextField(
                            value = phone,
                            onValueChange = onPhoneChange,
                            label = "Telefon",
                            isError = validation.phoneError != null,
                            supportingText = validation.phoneError?.message ?: "SMS dogrulama icin gerekli."
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        MockTextField(
                            value = password,
                            onValueChange = onPasswordChange,
                            label = "Parola",
                            isError = validation.passwordError != null,
                            supportingText = validation.passwordError?.message ?: "Guvenli bir sifre belirle."
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        PrimaryButton(
                            text = "Kayit Ol",
                            modifier = Modifier.fillMaxWidth(),
                            enabled = validation.canSubmit,
                            onClick = onRegister
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        SecondaryButton(text = "Giris Yap", modifier = Modifier.fillMaxWidth(), onClick = onGoToLogin)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                SecondaryButton(text = "Geri", modifier = Modifier.fillMaxWidth(), onClick = onBack)
            }
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                text = "Zaten hesabin var mi? Giris yap",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable(onClick = onGoToLogin),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun AuthModeToggle(
    loginSelected: Boolean,
    onLogin: () -> Unit,
    onRegister: () -> Unit
) {
    ScreenCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val loginColor = if (loginSelected) BrandBlue else MaterialTheme.colorScheme.surfaceVariant
            val registerColor = if (loginSelected) MaterialTheme.colorScheme.surfaceVariant else BrandBlue
            val loginText = if (loginSelected) Neutral0 else MaterialTheme.colorScheme.onSurfaceVariant
            val registerText = if (loginSelected) MaterialTheme.colorScheme.onSurfaceVariant else Neutral0

            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(loginColor, RoundedCornerShape(16.dp))
                    .clickable(onClick = onLogin)
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Giris Yap",
                    color = loginText,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(registerColor, RoundedCornerShape(16.dp))
                    .clickable(onClick = onRegister)
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Kayit Ol",
                    color = registerText,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun OtpScreen(
    code: String,
    onCodeChange: (String) -> Unit,
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    ScreenBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text("Telefonunu dogrula", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("+90 532 000 00 00 numarasina gonderdigimiz 6 haneli kodu gir.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = code,
                onValueChange = { onCodeChange(it.filter(Char::isDigit).take(6)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Kod") },
                singleLine = true
            )
            Spacer(modifier = Modifier.height(18.dp))
            PrimaryButton(text = "Dogrula ve Devam Et", modifier = Modifier.fillMaxWidth(), onClick = onContinue)
            Spacer(modifier = Modifier.height(12.dp))
            SecondaryButton(text = "Numarayi degistir", modifier = Modifier.fillMaxWidth(), onClick = onBack)
        }
    }
}

@Composable
fun LicenseScreen(
    status: LicenseStatusUiModel,
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    ScreenBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(status.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Text(status.subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(24.dp))

            ScreenCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(18.dp)) {
                    SectionTitle("Ehliyet on yuz", "Yukleme durumu")
                    Spacer(modifier = Modifier.height(12.dp))
                    MockUploadTile("On yuz", status.frontStatus, BrandMint)
                    Spacer(modifier = Modifier.height(12.dp))
                    MockUploadTile("Arka yuz", status.backStatus, BrandBlue)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Bilgilerin guvende saklanir. Dogrulama genelde birkac dakika surer.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            PrimaryButton(
                text = "Devam Et",
                modifier = Modifier.fillMaxWidth(),
                enabled = status.canContinue,
                onClick = onContinue
            )
            Spacer(modifier = Modifier.height(12.dp))
            SecondaryButton(text = "Geri", modifier = Modifier.fillMaxWidth(), onClick = onBack)
        }
    }
}

@Composable
fun MapScreen(
    appState: AppUiState,
    onVehicleSelected: (VehicleUiModel) -> Unit,
    onNavigate: (String) -> Unit
) {
    PageShell(
        title = "Ana harita",
        subtitle = "Yakindaki araclari goruntule",
        bottomBar = {
            BottomNavigationBar(
                currentRoute = AppDestination.Map.route,
                onNavigate = onNavigate
            )
        }
    ) { contentModifier ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(contentModifier)
                .padding(horizontal = 16.dp)
        ) {
            SearchBarPlaceholder()
            Spacer(modifier = Modifier.height(16.dp))
            ScreenCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                darkTint = Color(0xFF121A2A)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Harita gorunumu", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Kadikoy cevresinde 3 dk uzaklikta", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Column {
                        Text("Yakinda 12 arac", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(10.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(appState.vehicles) { vehicle ->
                                TextPill(
                                    text = "${vehicle.brand} ${vehicle.model}",
                                    selected = vehicle.id == appState.selectedVehicleId,
                                    onClick = { onVehicleSelected(vehicle) }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        PrimaryButton(
                            text = "En yakin araci bul",
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { onNavigate(AppDestination.VehicleDetail.route) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VehicleDetailScreen(
    vehicle: VehicleUiModel,
    onReserve: () -> Unit,
    onUnlock: () -> Unit,
    onBack: () -> Unit
) {
    ScreenBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            SectionTitle("${vehicle.brand} ${vehicle.model}", "${vehicle.plate} · ${vehicle.distance}")
            Spacer(modifier = Modifier.height(16.dp))
            ScreenCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    listOf(Color(0xFFFFD7D7), Color(0xFFB71313))
                                ),
                                shape = RoundedCornerShape(22.dp)
                            )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        InfoStat("Yakit", "%${vehicle.fuelPercent}", modifier = Modifier.weight(1f), highlight = true)
                        InfoStat("Menzil", "~${vehicle.rangeKm} km", modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        InfoStat("Vites", vehicle.transmission, modifier = Modifier.weight(1f))
                        InfoStat("Koltuk", "${vehicle.seats} kisi", modifier = Modifier.weight(1f))
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SecondaryButton(text = "Rezerve Et", modifier = Modifier.weight(1f), onClick = onReserve)
                PrimaryButton(text = "Kilidi Ac", modifier = Modifier.weight(1f), onClick = onUnlock)
            }
            Spacer(modifier = Modifier.height(12.dp))
            SecondaryButton(text = "Geri", modifier = Modifier.fillMaxWidth(), onClick = onBack)
        }
    }
}

@Composable
fun ReservationScreen(
    appState: AppUiState,
    onPlanSelected: (RentalPlan) -> Unit,
    onComplete: () -> Unit,
    onBack: () -> Unit
) {
    ScreenBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(18.dp))
            Text("Rezervasyon Onayi", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            ScreenCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(18.dp)) {
                    SectionTitle("${appState.vehicle.brand} ${appState.vehicle.model}", "${appState.vehicle.plate} · Manuel · 5 kisi")
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        RentalPlan.values().forEach { plan ->
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onPlanSelected(plan) }
                                    .background(
                                        if (appState.selectedPlan == plan) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                        RoundedCornerShape(18.dp)
                                    )
                                    .padding(14.dp)
                            ) {
                                Text(plan.label, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(plan.price, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            ScreenCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Ucretsiz rezervasyon")
                        Text("15 dk", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Baslangic ucreti")
                        Text("₺15,00", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Tahmini ucret (30 dk)")
                        Text("~₺135", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = true,
                    onCheckedChange = {},
                    colors = CheckboxDefaults.colors(checkedColor = BrandBlue)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Kullanim sartlarini ve kasko/sigorta kosullarini okudum, onayliyorum.")
            }

            Spacer(modifier = Modifier.height(18.dp))
            PrimaryButton(text = "Rezervasyonu Tamamla", modifier = Modifier.fillMaxWidth(), onClick = onComplete)
            Spacer(modifier = Modifier.height(12.dp))
            SecondaryButton(text = "Geri", modifier = Modifier.fillMaxWidth(), onClick = onBack)
        }
    }
}

@Composable
fun DeliveryPhotosScreen(
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    var selectedPhotos by remember { mutableStateOf(setOf("front", "back")) }

    ScreenBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Arac durumu", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Text("Baslamadan once 4 yonu cek", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(18.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                MockPhotoPanel("On", "front" in selectedPhotos, Modifier.weight(1f)) {
                    selectedPhotos = selectedPhotos.toggle("front")
                }
                MockPhotoPanel("Arka", "back" in selectedPhotos, Modifier.weight(1f)) {
                    selectedPhotos = selectedPhotos.toggle("back")
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                MockPhotoPanel("Sol", "left" in selectedPhotos, Modifier.weight(1f)) {
                    selectedPhotos = selectedPhotos.toggle("left")
                }
                MockPhotoPanel("Sag", "right" in selectedPhotos, Modifier.weight(1f)) {
                    selectedPhotos = selectedPhotos.toggle("right")
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text("Hasarlari net cek - teslim sonrasi anlasmazligi onler.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(16.dp))
            PrimaryButton(
                text = if (selectedPhotos.size < 4) "Kiralamayi Baslat · ${4 - selectedPhotos.size} foto kaldi" else "Kiralamayi Baslat",
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedPhotos.size >= 4,
                onClick = onContinue
            )
            Spacer(modifier = Modifier.height(12.dp))
            SecondaryButton(text = "Geri", modifier = Modifier.fillMaxWidth(), onClick = onBack)
        }
    }
}

@Composable
fun ActiveRentalScreen(
    activeRental: ActiveRentalUiModel,
    onFinish: () -> Unit,
    onUnlock: () -> Unit,
    onBack: () -> Unit
) {
    ScreenBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Spacer(modifier = Modifier.height(16.dp))
                Text(activeRental.vehicleLabel, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(18.dp))
                ScreenCard(modifier = Modifier.fillMaxWidth(), darkTint = Color(0xFF121A2A)) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text("Gecen sure", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(activeRental.elapsedTime, style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Black)
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            InfoStat("Anlik ucret", activeRental.currentFee, modifier = Modifier.weight(1f), highlight = true)
                            InfoStat("Mesafe", activeRental.distance, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
            Column {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    SecondaryButton(text = "Kilitle / Ac", modifier = Modifier.weight(1f), onClick = onUnlock)
                    PrimaryButton(text = "Kiralamayi Bitir", modifier = Modifier.weight(1f), onClick = onFinish)
                }
                Spacer(modifier = Modifier.height(12.dp))
                SecondaryButton(text = "Geri", modifier = Modifier.fillMaxWidth(), onClick = onBack)
            }
        }
    }
}

@Composable
fun PaymentSummaryScreen(
    onPay: () -> Unit,
    onBack: () -> Unit
) {
    ScreenBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(18.dp))
            Text("Yolculuk tamamlandi", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                InfoStat("Sure", "24 dk", modifier = Modifier.weight(1f), highlight = true)
                InfoStat("Mesafe", "12,4 km", modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(16.dp))
            ScreenCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Kiralama ucreti")
                        Text("₺108,00", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Baslangic ucreti")
                        Text("₺15,00", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Hizmet bedeli")
                        Text("₺7,50", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Indirim · ILKSURUS", color = BrandMint)
                        Text("-₺20,00", fontWeight = FontWeight.Bold, color = BrandMint)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    DividerSpacer()
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Toplam", fontWeight = FontWeight.Bold)
                        Text("₺110,50", fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleLarge)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            ScreenCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("VISA •••• 4291", fontWeight = FontWeight.Bold)
                        Text("Kisisel kart", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text("Degistir", color = MaterialTheme.colorScheme.primary)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            PrimaryButton(text = "₺110,50 Ode", modifier = Modifier.fillMaxWidth(), onClick = onPay)
            Spacer(modifier = Modifier.height(12.dp))
            SecondaryButton(text = "Geri", modifier = Modifier.fillMaxWidth(), onClick = onBack)
        }
    }
}

@Composable
fun WalletScreen(
    wallet: WalletUiModel,
    onNavigate: (String) -> Unit
) {
    PageShell(
        title = "Cuzdan",
        subtitle = "Bakiye ve kartlar",
        bottomBar = { BottomNavigationBar(currentRoute = AppDestination.Wallet.route, onNavigate = onNavigate) }
    ) { contentModifier ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .then(contentModifier)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                ScreenCard(modifier = Modifier.fillMaxWidth(), darkTint = Color(0xFF123C8A)) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Rencar bakiyesi", color = Color.White.copy(alpha = 0.85f))
                        Text(wallet.balance, style = MaterialTheme.typography.displaySmall, color = Color.White, fontWeight = FontWeight.Black)
                        Spacer(modifier = Modifier.height(14.dp))
                        PrimaryButton(text = "Bakiye Yukle", modifier = Modifier.fillMaxWidth(), onClick = {})
                    }
                }
            }
            item {
                SectionTitle("Kayitli kartlar")
            }
            items(wallet.cards) { card ->
                ScreenCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(card, fontWeight = FontWeight.Bold)
                            Text("Son kullanma 08/27", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        if (card.contains("4291")) {
                            TextPill(text = "Varsayilan", selected = true)
                        }
                    }
                }
            }
            item { SectionTitle("Son islemler") }
            items(wallet.transactions) { transaction ->
                ScreenCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(transaction, fontWeight = FontWeight.SemiBold)
                        Text(if (transaction.contains("-")) "₺110,50" else "+₺200,00", color = if (transaction.contains("-")) BrandRed else BrandMint, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryScreen(
    items: List<RentalHistoryUiModel>,
    onNavigate: (String) -> Unit
) {
    PageShell(
        title = "Kiralamalarim",
        subtitle = "Bu ay 6 yolculuk",
        bottomBar = { BottomNavigationBar(currentRoute = AppDestination.History.route, onNavigate = onNavigate) }
    ) { contentModifier ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .then(contentModifier)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items) { rental ->
                ScreenCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(rental.title, fontWeight = FontWeight.Bold)
                            Text(rental.date, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                TextPill(rental.duration)
                                TextPill(rental.distance)
                            }
                        }
                        Text(rental.price, fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileScreen(
    profile: ProfileUiModel,
    onNavigate: (String) -> Unit
) {
    PageShell(
        title = "Profil",
        subtitle = profile.name,
        bottomBar = { BottomNavigationBar(currentRoute = AppDestination.Profile.route, onNavigate = onNavigate) }
    ) { contentModifier ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(contentModifier)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ScreenCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(profile.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(profile.phone, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            ScreenCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(profile.licenseTitle, fontWeight = FontWeight.Bold)
                    Text(profile.licenseDetail, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            ScreenCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    ProfileMenuItem("Odeme yontemleri")
                    ProfileMenuItem("Ayarlar")
                    ProfileMenuItem("Yardim & destek")
                    ProfileMenuItem("Davet et · ₺50 kazan")
                }
            }
            ScreenCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Cikis yap",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    color = BrandRed,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun MockUploadTile(title: String, status: String, tint: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = tint.copy(alpha = 0.15f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(title, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Text(status, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(tint, RoundedCornerShape(14.dp))
            )
        }
    }
}

@Composable
private fun MockPhotoPanel(
    label: String,
    uploaded: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    ScreenCard(modifier = modifier.height(170.dp)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .clickable(onClick = onClick),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            TextPill(label, selected = uploaded)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(
                        if (uploaded) BrandMint.copy(alpha = 0.14f) else MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(18.dp)
                    )
            )
            Text(if (uploaded) "Yuklendi" else "Fotograf cek", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

private fun Set<String>.toggle(key: String): Set<String> = if (contains(key)) minus(key) else plus(key)

@Composable
private fun SearchBarPlaceholder() {
    ScreenCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Nereden arac alacaksin?", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun ProfileMenuItem(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text, fontWeight = FontWeight.SemiBold)
        Text(">", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
