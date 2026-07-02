package com.turkcell.rencar.feature.auth.presentation.register

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.turkcell.rencar.core.designsystem.PlusJakartaSans
import com.turkcell.rencar.core.designsystem.Sora
import com.turkcell.rencar.feature.auth.presentation.AuthViewModel

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
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
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

            // Name
            Text(text = "Ad Soyad", fontFamily = PlusJakartaSans, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = textSecondaryColor)
            Spacer(modifier = Modifier.height(9.dp))
            CustomTextField(
                value = name,
                onValueChange = { name = it; viewModel.nameInput = it },
                placeholder = "Ahmet Yılmaz",
                strokeColor = strokeColor,
                accentColor = accentColor,
                textPrimaryColor = textPrimaryColor,
                textSecondaryColor = textSecondaryColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email
            Text(text = "E-posta", fontFamily = PlusJakartaSans, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = textSecondaryColor)
            Spacer(modifier = Modifier.height(9.dp))
            CustomTextField(
                value = email,
                onValueChange = { email = it; viewModel.emailInput = it },
                placeholder = "ahmet@example.com",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                strokeColor = strokeColor,
                accentColor = accentColor,
                textPrimaryColor = textPrimaryColor,
                textSecondaryColor = textSecondaryColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Phone
            Text(text = "Telefon numarası", fontFamily = PlusJakartaSans, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = textSecondaryColor)
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
                        .border(1.5.dp, if (phone.isNotEmpty()) accentColor else strokeColor, RoundedCornerShape(15.dp))
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

            Spacer(modifier = Modifier.height(16.dp))

            // Password
            Text(text = "Parola", fontFamily = PlusJakartaSans, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = textSecondaryColor)
            Spacer(modifier = Modifier.height(9.dp))
            CustomTextField(
                value = password,
                onValueChange = { password = it; viewModel.passwordInput = it },
                placeholder = "••••••",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                strokeColor = strokeColor,
                accentColor = accentColor,
                textPrimaryColor = textPrimaryColor,
                textSecondaryColor = textSecondaryColor
            )

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = {
                    if (name.isNotBlank() && email.isNotBlank() && phone.isNotBlank() && password.length >= 6) {
                        viewModel.register(name, email, phone, password)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(
                        elevation = 14.dp,
                        shape = RoundedCornerShape(18.dp),
                        spotColor = Color(0xFF0B6BCB).copy(alpha = 0.3f)
                    ),
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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 28.dp)
                    .clickable { onLoginClick() },
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Zaten hesabın var mı? ",
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.5.sp,
                    color = textSecondaryColor
                )
                Text(
                    text = "Giriş yap",
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.5.sp,
                    color = accentColor
                )
            }
        }
    }
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    strokeColor: Color,
    accentColor: Color,
    textPrimaryColor: Color,
    textSecondaryColor: Color,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(1.5.dp, if (value.isNotEmpty()) accentColor else strokeColor, RoundedCornerShape(15.dp))
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            keyboardOptions = keyboardOptions,
            visualTransformation = visualTransformation,
            textStyle = TextStyle(
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = textPrimaryColor,
                letterSpacing = 0.5.sp
            ),
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = textSecondaryColor,
                        letterSpacing = 0.5.sp
                    )
                }
                innerTextField()
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
