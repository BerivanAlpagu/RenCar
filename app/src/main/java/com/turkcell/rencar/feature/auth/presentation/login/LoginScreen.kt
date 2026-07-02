package com.turkcell.rencar.feature.auth.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.turkcell.rencar.core.designsystem.PlusJakartaSans
import com.turkcell.rencar.core.designsystem.Sora
import com.turkcell.rencar.feature.auth.presentation.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onBackClick: () -> Unit,
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = isSystemInDarkTheme()
) {
    val backgroundColor = if (isDarkTheme) Color(0xFF0C0F14) else Color(0xFFFFFFFF)
    val textPrimaryColor = if (isDarkTheme) Color(0xFFF3F6FA) else Color(0xFF101620)
    val textSecondaryColor = if (isDarkTheme) Color(0xFF98A2B0) else Color(0xFF5C6675)
    val cardBgColor = if (isDarkTheme) Color(0xFF1B212A) else Color(0xFFF1F4F8)
    val strokeColor = if (isDarkTheme) Color(0xFF2A313B) else Color(0xFFE3E8EF)
    val accentColor = if (isDarkTheme) Color(0xFF4C95F0) else Color(0xFF0B6BCB)
    val noteTextColor = if (isDarkTheme) Color(0xFF7A828F) else Color(0xFF8A929E)

    var phone by remember { mutableStateOf(viewModel.phoneInput) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 14.dp, start = 28.dp, end = 28.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(cardBgColor, RoundedCornerShape(13.dp))
                    .clickable { onBackClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "←",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimaryColor
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Tekrar hoş geldin",
                fontFamily = Sora,
                fontWeight = FontWeight.Bold,
                fontSize = 27.sp,
                color = textPrimaryColor,
                letterSpacing = (-0.6).sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Telefon numaranı gir, SMS ile doğrulama kodu gönderelim.",
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                color = textSecondaryColor
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Telefon numarası",
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = textSecondaryColor
            )
            
            Spacer(modifier = Modifier.height(9.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(88.dp)
                        .height(56.dp)
                        .border(1.5.dp, strokeColor, RoundedCornerShape(15.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🇹🇷", fontSize = 18.sp)
                        Text(
                            text = "+90",
                            fontFamily = PlusJakartaSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = textPrimaryColor
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .border(1.5.dp, accentColor, RoundedCornerShape(15.dp))
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    BasicTextField(
                        value = phone,
                        onValueChange = {
                            phone = it
                            viewModel.phoneInput = it
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        textStyle = TextStyle(
                            fontFamily = PlusJakartaSans,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = textPrimaryColor,
                            letterSpacing = 0.5.sp
                        ),
                        decorationBox = { innerTextField ->
                            if (phone.isEmpty()) {
                                Text(
                                    text = "532 000 00 00",
                                    fontFamily = PlusJakartaSans,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp,
                                    color = textSecondaryColor,
                                    letterSpacing = 0.5.sp
                                )
                            }
                            innerTextField()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(9.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text(text = "ℹ️", fontSize = 14.sp)
                Text(
                    text = "6 haneli kodu bu numaraya göndereceğiz. SMS ücreti operatörüne bağlıdır.",
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.5.sp,
                    color = noteTextColor,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            Button(
                onClick = { if (phone.isNotBlank()) viewModel.login(phone) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(
                        elevation = 14.dp,
                        shape = RoundedCornerShape(18.dp),
                        spotColor = Color(0xFF0B6BCB).copy(alpha = 0.3f)
                    ),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0B6BCB),
                    contentColor = Color.White
                ),
                enabled = !viewModel.isLoading
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "📩", fontSize = 16.sp)
                        Text(
                            text = "Kod Gönder",
                            fontFamily = PlusJakartaSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.5.sp
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 28.dp)
                    .clickable { onRegisterClick() },
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Hesabın yok mu? ",
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.5.sp,
                    color = textSecondaryColor
                )
                Text(
                    text = "Kayıt ol",
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.5.sp,
                    color = accentColor
                )
            }
        }
    }
}
