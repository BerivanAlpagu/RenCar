package com.turkcell.rencar.feature.rentals.presentation.reservation

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
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ReservationConfirmationScreen(
    vehicleId: String,
    onBackClick: () -> Unit,
    onConfirmClick: (String) -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val bgColor = if (isDark) Color(0xFF0C0F14) else Color(0xFFF4F6F9)
    val topBarColor = if (isDark) Color(0xFF10151B) else Color(0xFFFFFFFF)
    val textColor = if (isDark) Color(0xFFF3F6FA) else Color(0xFF101620)
    val subTextColor = if (isDark) Color(0xFF98A2B0) else Color(0xFF5C6675)
    val cardColor = if (isDark) Color(0xFF171C24) else Color(0xFFFFFFFF)
    val borderColor = if (isDark) Color(0xFF232A33) else Color.Transparent
    val shadowColor = if (isDark) Color.Transparent else Color(0x0F101828)
    val badgeBg = if (isDark) Color(0xFF173726) else Color(0xFFE7F4EC)
    val badgeText = if (isDark) Color(0xFF34C98A) else Color(0xFF1A9E63)
    val activePlanBg = if (isDark) Color(0xFF14233A) else Color(0xFFEAF2FC)
    val inactivePlanBorder = if (isDark) Color(0xFF2A313B) else Color(0xFFE3E8EF)
    
    var isTermsAccepted by remember { mutableStateOf(true) }
    var selectedPlan by remember { mutableStateOf("minutely") } // minutely, hourly, daily

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
                    text = "Rezervasyon Onayı",
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
                    onClick = { if (isTermsAccepted) onConfirmClick(vehicleId) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(if (isDark) 30.dp else 26.dp, RoundedCornerShape(18.dp), spotColor = Color(0x4D0B6BCB)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isTermsAccepted) Color(0xFF0B6BCB) else Color.Gray
                    ),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text("Rezervasyonu Tamamla", fontWeight = FontWeight.Bold, fontSize = 16.5.sp, color = Color.White)
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
                    .then(if (isDark) Modifier.background(cardColor, RoundedCornerShape(20.dp)) else Modifier) // Fallback
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
                    Text("Renault Clio", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = textColor)
                    Spacer(modifier = Modifier.height(3.dp))
                    Text("34 RNC 022 · Manuel · 5 kişi", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = subTextColor)
                    Spacer(modifier = Modifier.height(7.dp))
                    Box(
                        modifier = Modifier
                            .background(badgeBg, RoundedCornerShape(7.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text("Yakıt %72", color = badgeText, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }

            // Rental Plan
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
                    // Minutely
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(if (selectedPlan == "minutely") activePlanBg else Color.Transparent, RoundedCornerShape(14.dp))
                            .then(
                                if (selectedPlan == "minutely") Modifier.border(1.6.dp, Color(0xFF0B6BCB), RoundedCornerShape(14.dp))
                                else Modifier.border(1.6.dp, inactivePlanBorder, RoundedCornerShape(14.dp))
                            )
                            .clickable { selectedPlan = "minutely" }
                            .padding(vertical = 11.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Dakikalık", fontSize = 15.sp, fontWeight = if (selectedPlan == "minutely") FontWeight.ExtraBold else FontWeight.Bold, color = if (selectedPlan == "minutely") Color(0xFF0B6BCB) else if (isDark) Color(0xFFB6BFCB) else Color(0xFF3A4452))
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("₺4,50/dk", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, color = if (selectedPlan == "minutely") Color(0xFF0B6BCB) else if (isDark) Color(0xFF7A828F) else Color(0xFF8A929E))
                    }
                    // Hourly
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(if (selectedPlan == "hourly") activePlanBg else Color.Transparent, RoundedCornerShape(14.dp))
                            .then(
                                if (selectedPlan == "hourly") Modifier.border(1.6.dp, Color(0xFF0B6BCB), RoundedCornerShape(14.dp))
                                else Modifier.border(1.6.dp, inactivePlanBorder, RoundedCornerShape(14.dp))
                            )
                            .clickable { selectedPlan = "hourly" }
                            .padding(vertical = 11.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Saatlik", fontSize = 15.sp, fontWeight = if (selectedPlan == "hourly") FontWeight.ExtraBold else FontWeight.Bold, color = if (selectedPlan == "hourly") Color(0xFF0B6BCB) else if (isDark) Color(0xFFB6BFCB) else Color(0xFF3A4452))
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("₺180/sa", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, color = if (selectedPlan == "hourly") Color(0xFF0B6BCB) else if (isDark) Color(0xFF7A828F) else Color(0xFF8A929E))
                    }
                    // Daily
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(if (selectedPlan == "daily") activePlanBg else Color.Transparent, RoundedCornerShape(14.dp))
                            .then(
                                if (selectedPlan == "daily") Modifier.border(1.6.dp, Color(0xFF0B6BCB), RoundedCornerShape(14.dp))
                                else Modifier.border(1.6.dp, inactivePlanBorder, RoundedCornerShape(14.dp))
                            )
                            .clickable { selectedPlan = "daily" }
                            .padding(vertical = 11.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Günlük", fontSize = 15.sp, fontWeight = if (selectedPlan == "daily") FontWeight.ExtraBold else FontWeight.Bold, color = if (selectedPlan == "daily") Color(0xFF0B6BCB) else if (isDark) Color(0xFFB6BFCB) else Color(0xFF3A4452))
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("₺1.450", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, color = if (selectedPlan == "daily") Color(0xFF0B6BCB) else if (isDark) Color(0xFF7A828F) else Color(0xFF8A929E))
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
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 11.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Başlangıç ücreti", color = subTextColor, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Text("₺15,00", color = textColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Tahmini ücret (30 dk)", color = subTextColor, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Text("~₺135", color = textColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Terms Checkbox
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp)
                    .clickable { isTermsAccepted = !isTermsAccepted },
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .background(if (isTermsAccepted) Color(0xFF0B6BCB) else Color(0xFFE3E8EF), RoundedCornerShape(7.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (isTermsAccepted) {
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
