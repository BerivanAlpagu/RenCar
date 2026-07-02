package com.turkcell.rencar.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.turkcell.rencar.ui.theme.PlusJakartaSans
import com.turkcell.rencar.ui.theme.Sora

@OptIn(ExperimentalMaterial3Api::class)
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

    var phone by remember { mutableStateOf(viewModel.phoneInput) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 28.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(14.dp))

            // Back button
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

            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = "Giriş Yap",
                fontFamily = Sora,
                fontWeight = FontWeight.Bold,
                fontSize = 27.sp,
                color = textPrimaryColor,
                letterSpacing = (-0.6).sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Devam etmek için kayıtlı telefon numaranı gir.",
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.Medium,
                fontSize = 14.5.sp,
                color = textSecondaryColor,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Phone Input
            OutlinedTextField(
                value = phone,
                onValueChange = {
                    phone = it
                    viewModel.phoneInput = it
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "+90 555 000 00 00",
                        color = textSecondaryColor,
                        fontFamily = PlusJakartaSans
                    )
                },
                shape = RoundedCornerShape(15.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = accentColor,
                    unfocusedBorderColor = strokeColor,
                    cursorColor = accentColor,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Hemen Başla button
            Button(
                onClick = { if (phone.isNotBlank()) viewModel.login(phone) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(
                        elevation = 14.dp,
                        shape = RoundedCornerShape(18.dp),
                        spotColor = Color(0xFF0B6BCB)
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
                    Text(
                        text = "Doğrulama Kodu Gönder",
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Register prompt
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onRegisterClick() },
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Henüz hesabın yok mu? ",
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = textSecondaryColor
                )
                Text(
                    text = "Kayıt ol",
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = accentColor
                )
            }
        }
    }
}
