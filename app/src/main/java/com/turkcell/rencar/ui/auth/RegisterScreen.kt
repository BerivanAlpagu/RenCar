package com.turkcell.rencar.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.turkcell.rencar.ui.theme.PlusJakartaSans
import com.turkcell.rencar.ui.theme.Sora

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = isSystemInDarkTheme()
) {
    val backgroundColor = if (isDarkTheme) Color(0xFF0C0F14) else Color(0xFFFFFFFF)
    val textPrimaryColor = if (isDarkTheme) Color(0xFFF3F6FA) else Color(0xFF101620)
    val textSecondaryColor = if (isDarkTheme) Color(0xFF98A2B0) else Color(0xFF5C6675)
    val cardBgColor = if (isDarkTheme) Color(0xFF1B212A) else Color(0xFFF1F4F8)
    val strokeColor = if (isDarkTheme) Color(0xFF2A313B) else Color(0xFFE3E8EF)
    val accentColor = if (isDarkTheme) Color(0xFF4C95F0) else Color(0xFF0B6BCB)

    var name by remember { mutableStateOf(viewModel.nameInput) }
    var email by remember { mutableStateOf(viewModel.emailInput) }
    var phone by remember { mutableStateOf(viewModel.phoneInput) }
    var password by remember { mutableStateOf(viewModel.passwordInput) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
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

            Spacer(modifier = Modifier.height(26.dp))

            Text(
                text = "Kayıt Ol",
                fontFamily = Sora,
                fontWeight = FontWeight.Bold,
                fontSize = 27.sp,
                color = textPrimaryColor,
                letterSpacing = (-0.6).sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Bilgilerini girerek Rencar ailesine hemen katıl.",
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.Medium,
                fontSize = 14.5.sp,
                color = textSecondaryColor,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Name Input
            Text(text = "Ad Soyad", fontFamily = PlusJakartaSans, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = textPrimaryColor)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it; viewModel.nameInput = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Ahmet Yılmaz", color = textSecondaryColor) },
                shape = RoundedCornerShape(15.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = accentColor,
                    unfocusedBorderColor = strokeColor,
                    cursorColor = accentColor,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email Input
            Text(text = "E-posta", fontFamily = PlusJakartaSans, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = textPrimaryColor)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it; viewModel.emailInput = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("ahmet@example.com", color = textSecondaryColor) },
                shape = RoundedCornerShape(15.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = accentColor,
                    unfocusedBorderColor = strokeColor,
                    cursorColor = accentColor,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Phone Input
            Text(text = "Telefon Numarası", fontFamily = PlusJakartaSans, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = textPrimaryColor)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it; viewModel.phoneInput = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("+905551112233", color = textSecondaryColor) },
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

            Spacer(modifier = Modifier.height(16.dp))

            // Password Input
            Text(text = "Parola", fontFamily = PlusJakartaSans, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = textPrimaryColor)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; viewModel.passwordInput = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("••••••", color = textSecondaryColor) },
                shape = RoundedCornerShape(15.dp),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = accentColor,
                    unfocusedBorderColor = strokeColor,
                    cursorColor = accentColor,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Register Button
            Button(
                onClick = {
                    if (name.isNotBlank() && email.isNotBlank() && phone.isNotBlank() && password.length >= 6) {
                        viewModel.register(name, email, phone, password)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(elevation = 14.dp, shape = RoundedCornerShape(18.dp), spotColor = Color(0xFF0B6BCB)),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0B6BCB), contentColor = Color.White),
                enabled = !viewModel.isLoading
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = "Kayıt Ol ve Giriş Yap",
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Login prompt
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onLoginClick() }
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Zaten hesabın var mı? ",
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = textSecondaryColor
                )
                Text(
                    text = "Giriş yap",
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = accentColor
                )
            }
        }
    }
}
