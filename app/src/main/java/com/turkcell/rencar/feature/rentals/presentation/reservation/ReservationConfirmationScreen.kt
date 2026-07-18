package com.turkcell.rencar.feature.rentals.presentation.reservation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.turkcell.rencar.feature.rentals.domain.model.RentalPlan

@Composable
fun ReservationConfirmationScreen(
    vehicleId: String,
    viewModel: ReservationConfirmationViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onUnlocked: (String) -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(vehicleId) {
        viewModel.onEvent(ReservationConfirmationEvent.LoadVehicle(vehicleId))
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ReservationConfirmationEffect.NavigateToHandover -> onUnlocked(effect.rentalId)
                is ReservationConfirmationEffect.ShowError -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                is ReservationConfirmationEffect.NavigateBack -> onBackClick()
            }
        }
    }

    val bgColor = if (isDark) Color(0xFF0C0F14) else Color(0xFFF4F6F9)
    val topBarColor = if (isDark) Color(0xFF10151B) else Color(0xFFFFFFFF)
    val textColor = if (isDark) Color(0xFFF3F6FA) else Color(0xFF101620)
    val subTextColor = if (isDark) Color(0xFF98A2B0) else Color(0xFF5C6675)
    val cardColor = if (isDark) Color(0xFF171C24) else Color(0xFFFFFFFF)
    val shadowColor = if (isDark) Color.Transparent else Color(0x0F101828)
    val badgeBg = if (isDark) Color(0xFF173726) else Color(0xFFE7F4EC)
    val badgeText = if (isDark) Color(0xFF34C98A) else Color(0xFF1A9E63)
    val activePlanBg = if (isDark) Color(0xFF14233A) else Color(0xFFEAF2FC)
    val inactivePlanBorder = if (isDark) Color(0xFF2A313B) else Color(0xFFE3E8EF)

    val vehicle = state.vehicle
    val canConfirm = state.isTermsAccepted && vehicle != null && !state.isConfirming

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = bgColor,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(topBarColor)
                    .padding(horizontal = 18.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(if (isDark) Color(0xFF1B212A) else Color(0xFFF1F4F8), RoundedCornerShape(13.dp))
                        .clickable { onBackClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = textColor
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = "Kilidi Aç",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(topBarColor)
                    .padding(14.dp)
                    .padding(bottom = 16.dp)
            ) {
                Button(
                    onClick = { viewModel.onEvent(ReservationConfirmationEvent.ConfirmClicked) },
                    enabled = canConfirm,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(if (isDark) 30.dp else 26.dp, RoundedCornerShape(18.dp), spotColor = Color(0x4D0B6BCB)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (canConfirm) Color(0xFF0B6BCB) else Color.Gray
                    ),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    if (state.isConfirming) {
                        CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                    } else {
                        Text("Kilidi Aç", fontWeight = FontWeight.Bold, fontSize = 16.5.sp, color = Color.White)
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Vehicle Info Card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(if (isDark) 0.dp else 18.dp, RoundedCornerShape(20.dp), spotColor = shadowColor)
                    .background(cardColor, RoundedCornerShape(20.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp, 72.dp)
                        .background(if (isDark) Color(0xFF10151B) else Color(0xFFF1F4F8), RoundedCornerShape(15.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Foto", color = subTextColor, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = if (vehicle != null) "${vehicle.brand} ${vehicle.model}" else "Araç yükleniyor…",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    if (vehicle != null) {
                        val transmissionText = if (vehicle.transmission == "AUTOMATIC") "Otomatik" else "Manuel"
                        Text(
                            "${vehicle.plate} · $transmissionText · ${vehicle.seats} kişi",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = subTextColor
                        )
                        Spacer(modifier = Modifier.height(7.dp))
                        Box(
                            modifier = Modifier
                                .background(badgeBg, RoundedCornerShape(7.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text("Yakıt %${vehicle.fuelPercent.toInt()}", color = badgeText, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }

            // Rental Plan
            if (vehicle != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(if (isDark) 0.dp else 18.dp, RoundedCornerShape(20.dp), spotColor = shadowColor)
                        .background(cardColor, RoundedCornerShape(20.dp))
                        .padding(16.dp)
                ) {
                    Text("Kiralama planı", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textColor)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                        PlanTab(
                            label = "Dakikalık",
                            priceText = "₺${vehicle.pricePerMinute}/dk",
                            isSelected = state.selectedPlan == RentalPlan.PER_MINUTE,
                            isDark = isDark,
                            activeBg = activePlanBg,
                            inactiveBorder = inactivePlanBorder,
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.onEvent(ReservationConfirmationEvent.PlanSelected(RentalPlan.PER_MINUTE)) }
                        )
                        PlanTab(
                            label = "Saatlik",
                            priceText = "₺${vehicle.pricePerHour}/sa",
                            isSelected = state.selectedPlan == RentalPlan.HOURLY,
                            isDark = isDark,
                            activeBg = activePlanBg,
                            inactiveBorder = inactivePlanBorder,
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.onEvent(ReservationConfirmationEvent.PlanSelected(RentalPlan.HOURLY)) }
                        )
                        PlanTab(
                            label = "Günlük",
                            priceText = "₺${vehicle.pricePerDay}",
                            isSelected = state.selectedPlan == RentalPlan.DAILY,
                            isDark = isDark,
                            activeBg = activePlanBg,
                            inactiveBorder = inactivePlanBorder,
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.onEvent(ReservationConfirmationEvent.PlanSelected(RentalPlan.DAILY)) }
                        )
                    }
                }
            }

            // Summary
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(if (isDark) 0.dp else 18.dp, RoundedCornerShape(20.dp), spotColor = shadowColor)
                    .background(cardColor, RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 11.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Ücretsiz rezervasyon", color = subTextColor, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Text("15 dk", color = textColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                val quote = state.quote
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 11.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Başlangıç ücreti", color = subTextColor, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Text(
                        if (quote != null) "₺${quote.startFee}" else if (state.isQuoteLoading) "…" else "-",
                        color = textColor, fontSize = 14.sp, fontWeight = FontWeight.Bold
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Tahmini ücret (${state.estimatedMinutes} dk)", color = subTextColor, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Text(
                        if (quote != null) "~₺${quote.estimatedTotal}" else if (state.isQuoteLoading) "…" else "-",
                        color = textColor, fontSize = 14.sp, fontWeight = FontWeight.Bold
                    )
                }
            }

            // Terms Checkbox
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp)
                    .clickable { viewModel.onEvent(ReservationConfirmationEvent.TermsToggled(!state.isTermsAccepted)) },
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .background(if (state.isTermsAccepted) Color(0xFF0B6BCB) else Color(0xFFE3E8EF), RoundedCornerShape(7.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (state.isTermsAccepted) {
                        Icon(Icons.Default.Check, contentDescription = "Checked", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
                Spacer(modifier = Modifier.width(11.dp))
                Text(
                    text = "Kullanım şartlarını ve kasko/sigorta koşullarını okudum, onaylıyorum.",
                    color = subTextColor,
                    fontSize = 12.5.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun PlanTab(
    label: String,
    priceText: String,
    isSelected: Boolean,
    isDark: Boolean,
    activeBg: Color,
    inactiveBorder: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .background(if (isSelected) activeBg else Color.Transparent, RoundedCornerShape(14.dp))
            .then(
                if (isSelected) Modifier.border(1.6.dp, Color(0xFF0B6BCB), RoundedCornerShape(14.dp))
                else Modifier.border(1.6.dp, inactiveBorder, RoundedCornerShape(14.dp))
            )
            .clickable { onClick() }
            .padding(vertical = 11.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            label,
            fontSize = 15.sp,
            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
            color = if (isSelected) Color(0xFF0B6BCB) else if (isDark) Color(0xFFB6BFCB) else Color(0xFF3A4452)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            priceText,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isSelected) Color(0xFF0B6BCB) else if (isDark) Color(0xFF7A828F) else Color(0xFF8A929E)
        )
    }
}
