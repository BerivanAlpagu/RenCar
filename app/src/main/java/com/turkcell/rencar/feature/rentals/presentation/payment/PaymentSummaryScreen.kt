package com.turkcell.rencar.feature.rentals.presentation.payment

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
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PaymentSummaryScreen(
    onPayClick: () -> Unit,
    onCloseClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val bgColor = if (isDark) Color(0xFF0C0F14) else Color(0xFFF4F6F9)
    val topBarColor = if (isDark) Color(0xFF10151B) else Color(0xFFFFFFFF)
    val cardColor = if (isDark) Color(0xFF171C24) else Color(0xFFFFFFFF)
    val textColor = if (isDark) Color(0xFFF3F6FA) else Color(0xFF101620)
    val subTextColor = if (isDark) Color(0xFF98A2B0) else Color(0xFF5C6675)
    
    val shadowColor = if (isDark) Color.Transparent else Color(0x0D101828)

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
                // Invisible spacer for centering
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
                    onClick = { onPayClick() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(if (isDark) 30.dp else 26.dp, RoundedCornerShape(18.dp), spotColor = Color(0x4D0B6BCB)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0B6BCB)),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text("₺110,50 Öde", fontWeight = FontWeight.Bold, fontSize = 16.5.sp, color = Color.White)
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
                Text("Renault Clio · 34 RNC 022", fontSize = 13.5.sp, fontWeight = FontWeight.Medium, color = subTextColor)
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
                    Text("24 dk", fontSize = 18.sp, color = textColor, fontWeight = FontWeight.ExtraBold)
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
                    Text("12,4 km", fontSize = 18.sp, color = textColor, fontWeight = FontWeight.ExtraBold)
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
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Kiralama ücreti (24 dk)", fontSize = 13.5.sp, color = subTextColor, fontWeight = FontWeight.Medium)
                    Text("₺108,00", fontSize = 13.5.sp, color = textColor, fontWeight = FontWeight.Bold)
                }
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Başlangıç ücreti", fontSize = 13.5.sp, color = subTextColor, fontWeight = FontWeight.Medium)
                    Text("₺15,00", fontSize = 13.5.sp, color = textColor, fontWeight = FontWeight.Bold)
                }
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Hizmet bedeli", fontSize = 13.5.sp, color = subTextColor, fontWeight = FontWeight.Medium)
                    Text("₺7,50", fontSize = 13.5.sp, color = textColor, fontWeight = FontWeight.Bold)
                }
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("İndirim · İLKSÜRÜŞ", fontSize = 13.5.sp, color = if (isDark) Color(0xFF34C98A) else Color(0xFF1A9E63), fontWeight = FontWeight.SemiBold)
                    Text("−₺20,00", fontSize = 13.5.sp, color = if (isDark) Color(0xFF34C98A) else Color(0xFF1A9E63), fontWeight = FontWeight.Bold)
                }
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(if (isDark) Color(0xFF2C333D) else Color(0xFFE3E8EF)))
                Row(modifier = Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Toplam", fontSize = 15.sp, color = textColor, fontWeight = FontWeight.Bold)
                    Text("₺110,50", fontSize = 22.sp, color = textColor, fontWeight = FontWeight.ExtraBold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Card
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
                    Text("VISA", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, fontStyle = FontStyle.Italic)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("•••• 4291", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textColor)
                    Text("Kişisel kart", fontSize = 11.5.sp, fontWeight = FontWeight.Medium, color = if (isDark) Color(0xFF7A828F) else Color(0xFF8A929E))
                }
                Text("Değiştir", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (isDark) Color(0xFF4C95F0) else Color(0xFF0B6BCB))
            }
        }
    }
}
