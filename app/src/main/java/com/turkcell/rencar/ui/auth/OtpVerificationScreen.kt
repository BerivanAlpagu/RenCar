package com.turkcell.rencar.ui.auth

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.turkcell.rencar.ui.theme.PlusJakartaSans
import com.turkcell.rencar.ui.theme.Sora

@Composable
fun OtpVerificationScreen(
    phone: String,
    viewModel: AuthViewModel,
    onBackClick: () -> Unit,
    onChangePhoneClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = isSystemInDarkTheme()
) {
    val backgroundColor = if (isDarkTheme) Color(0xFF0C0F14) else Color(0xFFFFFFFF)
    val textPrimaryColor = if (isDarkTheme) Color(0xFFF3F6FA) else Color(0xFF101620)
    val textSecondaryColor = if (isDarkTheme) Color(0xFF98A2B0) else Color(0xFF5C6675)
    val cardBgColor = if (isDarkTheme) Color(0xFF1B212A) else Color(0xFFF1F4F8)
    val strokeColor = if (isDarkTheme) Color(0xFF2A313B) else Color(0xFFE3E8EF)
    val accentColor = if (isDarkTheme) Color(0xFF4C95F0) else Color(0xFF0B6BCB)
    val activeBoxBg = if (isDarkTheme) Color(0xFF14233A) else Color(0xFFF7FAFE)
    val timerRowColor = if (isDarkTheme) Color(0xFF7A828F) else Color(0xFF8A929E)

    var code by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    // Pulse cursor animation
    val infiniteTransition = rememberInfiniteTransition(label = "cursor")
    val cursorAlpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1000
                0.7f at 500
            },
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursorAlpha"
    )

    // Request keyboard focus immediately
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

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

            // SMS Symbol Box
            Spacer(modifier = Modifier.height(26.dp))
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        if (isDarkTheme) Color(0xFF14233A) else Color(0xFFEAF2FC),
                        RoundedCornerShape(18.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                // SMS phone checking symbol
                Text(
                    text = "📲",
                    fontSize = 28.sp
                )
            }

            Spacer(modifier = Modifier.height(22.dp))
            Text(
                text = "Telefonunu doğrula",
                fontFamily = Sora,
                fontWeight = FontWeight.Bold,
                fontSize = 27.sp,
                color = textPrimaryColor,
                letterSpacing = (-0.6).sp
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                wrapAlternativeRow = true
            ) {
                Text(
                    text = "$phone numarasına gönderdiğimiz 6 haneli kodu gir.",
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.5.sp,
                    color = textSecondaryColor,
                    lineHeight = 22.sp
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Hidden BasicTextField for receiving input
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                BasicTextField(
                    value = code,
                    onValueChange = {
                        if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                            code = it
                            viewModel.otpCode = it
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .size(1.dp), // keep it tiny & hidden
                    decorationBox = { it() }
                )

                // Visual boxes
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { focusRequester.requestFocus() },
                    horizontalArrangement = Arrangement.spacedBy(9.dp)
                ) {
                    repeat(6) { index ->
                        val char = code.getOrNull(index)?.toString() ?: ""
                        val isFocused = code.length == index

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(62.dp)
                                .clip(RoundedCornerShape(15.dp))
                                .background(if (isFocused) activeBoxBg else Color.Transparent)
                                .border(
                                    width = if (isFocused) 2.dp else 1.5.dp,
                                    color = if (isFocused) accentColor else strokeColor,
                                    shape = RoundedCornerShape(15.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isFocused) {
                                // Blinking Cursor
                                Box(
                                    modifier = Modifier
                                        .width(2.dp)
                                        .height(26.dp)
                                        .background(accentColor.copy(alpha = cursorAlpha))
                                )
                            } else {
                                Text(
                                    text = char,
                                    fontFamily = Sora,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp,
                                    color = textPrimaryColor,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Countdown timer row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable(enabled = viewModel.timerSeconds == 0) {
                    viewModel.login(phone) // Resend code
                }
            ) {
                Text(
                    text = "⏰  ",
                    fontSize = 16.sp
                )
                Text(
                    text = if (viewModel.timerSeconds > 0) {
                        "Kodu tekrar gönder · "
                    } else {
                        "Kodu tekrar gönder"
                    },
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = timerRowColor
                )
                if (viewModel.timerSeconds > 0) {
                    val minutes = viewModel.timerSeconds / 60
                    val seconds = viewModel.timerSeconds % 60
                    val formattedTime = String.format("%d:%02d", minutes, seconds)
                    Text(
                        text = formattedTime,
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = textSecondaryColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Confirm Button
            Button(
                onClick = { if (code.length == 6) viewModel.verifyOtp(phone, code) },
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
                enabled = code.length == 6 && !viewModel.isLoading
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = "Doğrula ve Devam Et",
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Change number row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onChangePhoneClick() },
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Numara yanlış mı? ",
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = textSecondaryColor
                )
                Text(
                    text = "Değiştir",
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = accentColor
                )
            }
        }
    }
}

@Composable
private fun Row(
    modifier: Modifier = Modifier,
    wrapAlternativeRow: Boolean = false,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    content: @Composable RowScope.() -> Unit
) {
    androidx.compose.foundation.layout.Row(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment,
        content = content
    )
}
