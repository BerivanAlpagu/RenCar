package com.turkcell.rencar.feature.rentals.presentation.active

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
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
import java.util.Locale

@Composable
fun ActiveRentalScreen(
    rentalId: String,
    viewModel: ActiveRentalViewModel,
    onFinishRentalClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val isDark = isSystemInDarkTheme()
    val context = LocalContext.current

    LaunchedEffect(rentalId) {
        viewModel.onEvent(ActiveRentalEvent.ScreenOpened(rentalId))
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ActiveRentalEffect.NavigateToReturnPhoto -> onFinishRentalClick()
                is ActiveRentalEffect.ShowError -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    if (state.isLoading && state.rental == null) {
        Box(modifier = Modifier.fillMaxSize().background(if (isDark) Color(0xFF0C0F14) else Color(0xFFF3F5F8)), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFF0B6BCB))
        }
        return
    }

    val bgColor = if (isDark) Color(0xFF10151B) else Color(0xFFE9EDF2)
    val cardColor = if (isDark) Color(0xFF171C24) else Color(0xFFFFFFFF)
    val textColor = if (isDark) Color(0xFFF3F6FA) else Color(0xFF101620)
    val subTextColor = if (isDark) Color(0xFF98A2B0) else Color(0xFF5C6675)

    val vehicleLabel = state.rental?.let { "${it.vehicle.brand} ${it.vehicle.model}" } ?: "Araç"
    val hours = state.elapsedSeconds / 3600
    val minutes = (state.elapsedSeconds % 3600) / 60
    val seconds = state.elapsedSeconds % 60
    val passedTime = String.format(Locale("tr", "TR"), "%02d:%02d:%02d", hours, minutes, seconds)
    val passedDistance = String.format(Locale("tr", "TR"), "%.1f km", state.distanceKm)
    val totalCost = String.format(Locale("tr", "TR"), "₺%.2f", state.currentCost)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = bgColor
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Backdrop
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 220.dp)
                    .background(if (isDark) Color(0xFF11161D) else Color(0xFFE6EBF1))
            )

            // Status Badge
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 52.dp)
                    .shadow(20.dp, RoundedCornerShape(30.dp), spotColor = Color(0x66000000))
                    .background(if (isDark) Color.White else Color(0xFF101620), RoundedCornerShape(30.dp))
                    .padding(horizontal = 18.dp, vertical = 9.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).background(Color(0xFF1FB370), CircleShape))
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "Kiralama aktif · $vehicleLabel",
                        color = if (isDark) Color(0xFF101620) else Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Bottom Info Card
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .shadow(40.dp, RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp), spotColor = Color(0x24101828))
                    .background(cardColor, RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                    .padding(18.dp, 22.dp, 18.dp, 30.dp)
            ) {
                // Drag handle
                Box(
                    modifier = Modifier
                        .size(42.dp, 5.dp)
                        .background(if (isDark) Color(0xFF2C333D) else Color(0xFFE0E5EC), RoundedCornerShape(3.dp))
                        .align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(18.dp))

                // Time
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("Geçen süre", fontSize = 13.sp, color = subTextColor, fontWeight = FontWeight.SemiBold)
                    Text(passedTime, fontSize = 46.sp, color = textColor, fontWeight = FontWeight.ExtraBold, letterSpacing = (-1).sp)
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Stats
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(11.dp)) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(if (isDark) Color(0xFF1F262F) else Color(0xFFF4F6F9), RoundedCornerShape(16.dp))
                            .padding(13.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Anlık ücret", fontSize = 11.5.sp, color = subTextColor, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(totalCost, fontSize = 20.sp, color = Color(0xFF0B6BCB), fontWeight = FontWeight.ExtraBold)
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(if (isDark) Color(0xFF1F262F) else Color(0xFFF4F6F9), RoundedCornerShape(16.dp))
                            .padding(13.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Mesafe", fontSize = 11.5.sp, color = subTextColor, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(passedDistance, fontSize = 20.sp, color = textColor, fontWeight = FontWeight.ExtraBold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .shadow(24.dp, RoundedCornerShape(16.dp), spotColor = Color(0x4DE5484D))
                        .background(Color(0xFFE5484D), RoundedCornerShape(16.dp))
                        .clickable { viewModel.onEvent(ActiveRentalEvent.FinishClicked) },
                    contentAlignment = Alignment.Center
                ) {
                    Text("Kiralamayı Bitir", fontSize = 14.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
