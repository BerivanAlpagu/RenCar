package com.turkcell.rencar.feature.rentals.presentation.payment

import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.turkcell.rencar.feature.rentals.domain.model.RentalPaymentMethod
import java.util.Locale

@Composable
fun PaymentSummaryScreen(
    rentalId: String,
    viewModel: PaymentSummaryViewModel = hiltViewModel(),
    onPayClick: () -> Unit,
    onCloseClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(rentalId) {
        viewModel.onEvent(PaymentSummaryEvent.LoadSummary(rentalId))
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is PaymentSummaryEffect.NavigateHome -> onPayClick()
                is PaymentSummaryEffect.ShowError -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val bgColor = if (isDark) Color(0xFF0C0F14) else Color(0xFFF4F6F9)
    val topBarColor = if (isDark) Color(0xFF10151B) else Color(0xFFFFFFFF)
    val cardColor = if (isDark) Color(0xFF171C24) else Color(0xFFFFFFFF)
    val textColor = if (isDark) Color(0xFFF3F6FA) else Color(0xFF101620)
    val subTextColor = if (isDark) Color(0xFF98A2B0) else Color(0xFF5C6675)

    val shadowColor = if (isDark) Color.Transparent else Color(0x0D101828)

    val rental = state.rental
    val locale = Locale("tr", "TR")
    val totalPrice = rental?.totalPrice ?: 0.0
    val payLabel = if (state.isPaying) "…" else "₺${String.format(locale, "%.2f", totalPrice)} Öde"

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = bgColor,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(topBarColor)
                    .padding(horizontal = 18.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.size(42.dp))
                Text(
                    text = "Ödeme Özeti",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(if (isDark) Color(0xFF1B212A) else Color(0xFFF1F4F8), RoundedCornerShape(13.dp))
                        .clickable { onCloseClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = textColor
                    )
                }
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
                    onClick = { viewModel.onEvent(PaymentSummaryEvent.PayClicked) },
                    enabled = rental != null && rental.paymentStatus.name != "PAID" && !state.isPaying,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(if (isDark) 30.dp else 26.dp, RoundedCornerShape(18.dp), spotColor = Color(0x4D0B6BCB)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0B6BCB)),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    if (state.isPaying) {
                        CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                    } else {
                        Text(payLabel, fontWeight = FontWeight.Bold, fontSize = 16.5.sp, color = Color.White)
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 18.dp, vertical = 16.dp)
        ) {
            // Header
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(62.dp)
                        .background(if (isDark) Color(0xFF152C20) else Color(0xFFE7F4EC), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(Color(0xFF1FB370), CircleShape)
                            .shadow(18.dp, CircleShape, spotColor = Color(0x801FB370)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Success", tint = Color.White, modifier = Modifier.size(22.dp))
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text("Yolculuk tamamlandı", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textColor)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    rental?.let { "${it.vehicle.brand} ${it.vehicle.model} · ${it.vehicle.plate}" } ?: "Yükleniyor…",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = subTextColor
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Duration & Distance
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(11.dp)) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(cardColor, RoundedCornerShape(16.dp))
                        .shadow(14.dp, RoundedCornerShape(16.dp), spotColor = shadowColor)
                        .padding(13.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Süre", fontSize = 11.5.sp, color = subTextColor, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(3.dp))
                    Text("${rental?.durationMinutes ?: 0} dk", fontSize = 18.sp, color = textColor, fontWeight = FontWeight.ExtraBold)
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(cardColor, RoundedCornerShape(16.dp))
                        .shadow(14.dp, RoundedCornerShape(16.dp), spotColor = shadowColor)
                        .padding(13.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Mesafe", fontSize = 11.5.sp, color = subTextColor, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        String.format(locale, "%.1f km", rental?.distanceKm ?: 0.0),
                        fontSize = 18.sp, color = textColor, fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Receipt
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(cardColor, RoundedCornerShape(20.dp))
                    .shadow(14.dp, RoundedCornerShape(20.dp), spotColor = shadowColor)
                    .padding(16.dp)
            ) {
                val usageFee = (rental?.totalPrice ?: 0.0) - (rental?.startFee ?: 0.0) - (rental?.serviceFee ?: 0.0)
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Kullanım ücreti (${rental?.durationMinutes ?: 0} dk)", fontSize = 13.5.sp, color = subTextColor, fontWeight = FontWeight.Medium)
                    Text("₺${String.format(locale, "%.2f", usageFee)}", fontSize = 13.5.sp, color = textColor, fontWeight = FontWeight.Bold)
                }
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Başlangıç ücreti", fontSize = 13.5.sp, color = subTextColor, fontWeight = FontWeight.Medium)
                    Text("₺${String.format(locale, "%.2f", rental?.startFee ?: 0.0)}", fontSize = 13.5.sp, color = textColor, fontWeight = FontWeight.Bold)
                }
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Hizmet bedeli", fontSize = 13.5.sp, color = subTextColor, fontWeight = FontWeight.Medium)
                    Text("₺${String.format(locale, "%.2f", rental?.serviceFee ?: 0.0)}", fontSize = 13.5.sp, color = textColor, fontWeight = FontWeight.Bold)
                }
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(if (isDark) Color(0xFF2C333D) else Color(0xFFE3E8EF)))
                Row(modifier = Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Toplam", fontSize = 15.sp, color = textColor, fontWeight = FontWeight.Bold)
                    Text("₺${String.format(locale, "%.2f", totalPrice)}", fontSize = 22.sp, color = textColor, fontWeight = FontWeight.ExtraBold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Payment method selector
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                PaymentMethodChip(
                    label = "Cüzdan (₺${state.walletBalance?.let { String.format(locale, "%.2f", it) } ?: "-"})",
                    isSelected = state.selectedMethod == RentalPaymentMethod.WALLET,
                    isDark = isDark,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.onEvent(PaymentSummaryEvent.MethodSelected(RentalPaymentMethod.WALLET)) }
                )
                PaymentMethodChip(
                    label = "Kart",
                    isSelected = state.selectedMethod == RentalPaymentMethod.CARD,
                    isDark = isDark,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.onEvent(PaymentSummaryEvent.MethodSelected(RentalPaymentMethod.CARD)) }
                )
            }

            if (state.selectedMethod == RentalPaymentMethod.CARD) {
                Spacer(modifier = Modifier.height(14.dp))
                val selectedCard = state.cards.firstOrNull { it.id == state.selectedCardId }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(cardColor, RoundedCornerShape(16.dp))
                        .shadow(14.dp, RoundedCornerShape(16.dp), spotColor = shadowColor)
                        .padding(horizontal = 14.dp, vertical = 13.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp, 28.dp)
                            .background(Brush.linearGradient(listOf(Color(0xFF1A1F71), Color(0xFF0B6BCB))), RoundedCornerShape(6.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(selectedCard?.cardType?.name ?: "-", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, fontStyle = FontStyle.Italic)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            selectedCard?.let { "•••• ${it.lastFour}" } ?: "Kayıtlı kart yok",
                            fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textColor
                        )
                        Text("Kişisel kart", fontSize = 11.5.sp, fontWeight = FontWeight.Medium, color = if (isDark) Color(0xFF7A828F) else Color(0xFF8A929E))
                    }
                    if (state.cards.size > 1) {
                        Text(
                            "Değiştir",
                            fontSize = 13.sp, fontWeight = FontWeight.Bold,
                            color = if (isDark) Color(0xFF4C95F0) else Color(0xFF0B6BCB),
                            modifier = Modifier.clickable {
                                val currentIndex = state.cards.indexOfFirst { it.id == state.selectedCardId }
                                val next = state.cards[(currentIndex + 1) % state.cards.size]
                                viewModel.onEvent(PaymentSummaryEvent.CardSelected(next.id))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PaymentMethodChip(
    label: String,
    isSelected: Boolean,
    isDark: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .background(
                if (isSelected) Color(0xFF0B6BCB) else if (isDark) Color(0xFF171C24) else Color.White,
                RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.White else if (isDark) Color(0xFFB6BFCB) else Color(0xFF3A4452)
        )
    }
}
