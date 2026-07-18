package com.turkcell.rencar.feature.wallet.presentation.wallet

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.turkcell.rencar.feature.wallet.domain.model.CardType
import com.turkcell.rencar.feature.wallet.domain.model.PaymentCard
import com.turkcell.rencar.feature.wallet.domain.model.WalletTransaction
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    modifier: Modifier = Modifier,
    viewModel: WalletViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is WalletEffect.ShowSnackbar -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val isDark = MaterialTheme.colorScheme.background == Color(0xFF121212)
    val backgroundColor = if (isDark) Color(0xFF0C0F14) else Color(0xFFF4F6F9)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        if (state.isLoading || state.walletInfo == null) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            val wallet = state.walletInfo!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                
                // Title
                Text(
                    text = "Cüzdan",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color(0xFFF3F6FA) else Color(0xFF101620),
                    letterSpacing = (-0.5).sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable content
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Balance Card Item
                    item {
                        BalanceCard(
                            balance = wallet.balance,
                            isDark = isDark,
                            onTopUpClick = { viewModel.onEvent(WalletEvent.TopUpButtonClicked) }
                        )
                    }

                    // Saved Cards Header
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp, bottom = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Kayıtlı kartlar",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color(0xFFF3F6FA) else Color(0xFF101620)
                            )
                            Text(
                                text = "+ Ekle",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color(0xFF4C95F0) else Color(0xFF0B6BCB),
                                modifier = Modifier.clickable {
                                    viewModel.onEvent(WalletEvent.AddCardButtonClicked)
                                }
                            )
                        }
                    }

                    // Saved Cards Items
                    items(wallet.cards) { card ->
                        PaymentCardItem(card = card, isDark = isDark)
                    }

                    // Transactions Header
                    item {
                        Text(
                            text = "Son işlemler",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color(0xFFF3F6FA) else Color(0xFF101620),
                            modifier = Modifier.padding(top = 10.dp, bottom = 2.dp)
                        )
                    }

                    // Transactions List Wrapper
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isDark) Color(0xFF171C24) else Color(0xFFFFFFFF)
                            ),
                            border = if (isDark) BorderStroke(1.dp, Color(0xFF232A33)) else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(
                                    elevation = if (isDark) 0.dp else 2.dp,
                                    shape = RoundedCornerShape(16.dp),
                                    clip = false
                                )
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 14.dp)
                            ) {
                                wallet.transactions.forEachIndexed { index, transaction ->
                                    TransactionItem(
                                        transaction = transaction,
                                        isDark = isDark,
                                        isLast = index == wallet.transactions.lastIndex
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Add Balance Dialog Overlay
        if (state.showAddBalanceSheet) {
            AddBalanceDialog(
                onDismiss = { viewModel.onEvent(WalletEvent.DismissAddBalanceSheet) },
                onConfirm = { amount -> viewModel.onEvent(WalletEvent.AddBalanceClicked(amount)) }
            )
        }

        if (state.showAddCardSheet) {
            AddCardDialog(
                isLoading = state.isAddingCard,
                onDismiss = { viewModel.onEvent(WalletEvent.DismissAddCardSheet) },
                onConfirm = { brand, last4, expMonth, expYear ->
                    viewModel.onEvent(WalletEvent.AddCardClicked(brand, last4, expMonth, expYear))
                }
            )
        }
    }
}

@Composable
fun BalanceCard(
    balance: Double,
    isDark: Boolean,
    onTopUpClick: () -> Unit
) {
    val gradientBrush = if (isDark) {
        Brush.linearGradient(
            colors = listOf(Color(0xFF2479DC), Color(0xFF0B5AAE)),
            start = Offset(0f, 0f),
            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(Color(0xFF1E7FE0), Color(0xFF0B6BCB)),
            start = Offset(0f, 0f),
            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
        )
    }

    val shadowColor = if (isDark) Color(0x660B6BCB) else Color(0x520B6BCB)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(22.dp),
                ambientColor = shadowColor,
                spotColor = shadowColor
            )
            .clip(RoundedCornerShape(22.dp))
            .background(gradientBrush)
            .padding(20.dp)
    ) {
        // Overlay circular visual element
        Box(
            modifier = Modifier
                .size(140.dp)
                .offset(x = 60.dp, y = (-60).dp)
                .clip(CircleShape)
                .background(
                    color = if (isDark) Color.White.copy(alpha = 0.10f) else Color.White.copy(alpha = 0.12f)
                )
                .align(Alignment.TopEnd)
        )

        Column {
            Text(
                text = "Rencar bakiyesi",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.8f)
            )
            
            val formattedBalance = String.format(Locale("tr", "TR"), "%.2f", balance)
            Text(
                text = "₺$formattedBalance",
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = (-1).sp,
                modifier = Modifier.padding(top = 4.dp)
            )
            
            Spacer(modifier = Modifier.height(18.dp))
            
            // Add Balance button
            Surface(
                onClick = onTopUpClick,
                color = Color.White.copy(alpha = 0.18f),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "AddIcon",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Bakiye Yükle",
                        color = Color.White,
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun PaymentCardItem(
    card: PaymentCard,
    isDark: Boolean
) {
    val cardBackground = if (isDark) Color(0xFF171C24) else Color(0xFFFFFFFF)
    val density = LocalDensity.current.density
    
    val cardBorderModifier = if (isDark) {
        Modifier.border(1.dp, Color(0xFF232A33), RoundedCornerShape(16.dp))
    } else {
        Modifier.shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp), clip = false)
    }

    Surface(
        color = cardBackground,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .then(cardBorderModifier)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 13.dp)
        ) {
            // Card Logo placeholder
            val logoBrush = when (card.cardType) {
                CardType.VISA -> Brush.linearGradient(listOf(Color(0xFF1A1F71), Color(0xFF0B6BCB)))
                CardType.MASTERCARD -> Brush.linearGradient(listOf(Color(0xFFEB001B), Color(0xFFF79E1B)))
            }
            
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(width = 40.dp, height = 28.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(logoBrush)
            ) {
                Text(
                    text = if (card.cardType == CardType.VISA) "VISA" else "MC",
                    color = Color.White,
                    fontSize = if (card.cardType == CardType.VISA) 9.sp else 8.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontStyle = if (card.cardType == CardType.VISA) FontStyle.Italic else FontStyle.Normal
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Card Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "•••• ${card.lastFour}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color(0xFFF3F6FA) else Color(0xFF101620)
                )
                Text(
                    text = "Son kullanma ${card.expiryDate}",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isDark) Color(0xFF7A828F) else Color(0xFF8A929E)
                )
            }

            // Default Badge
            if (card.isDefault) {
                Box(
                    modifier = Modifier
                        .background(
                            color = if (isDark) Color(0xFF173726) else Color(0xFFE7F4EC),
                            shape = RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "Varsayılan",
                        color = if (isDark) Color(0xFF34C98A) else Color(0xFF1A9E63),
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionItem(
    transaction: WalletTransaction,
    isDark: Boolean,
    isLast: Boolean
) {
    val density = LocalDensity.current.density
    val isPositive = transaction.amount > 0
    val iconBackground = if (isPositive) {
        if (isDark) Color(0xFF152C20) else Color(0xFFE7F4EC)
    } else {
        if (isDark) Color(0xFF2E1A1B) else Color(0xFFFBEDED)
    }
    val iconColor = if (isPositive) {
        if (isDark) Color(0xFF34C98A) else Color(0xFF1A9E63)
    } else {
        if (isDark) Color(0xFFF0575B) else Color(0xFFE5484D)
    }

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 11.dp)
        ) {
            // Custom drawn Icon Box
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .background(iconBackground)
            ) {
                Canvas(modifier = Modifier.size(18.dp)) {
                    val scaleX = size.width / 24f
                    val scaleY = size.height / 24f
                    
                    if (isPositive) {
                        // Plus Icon path: d="M12 5v14M5 12h14"
                        drawLine(
                            color = iconColor,
                            start = Offset(12f * scaleX, 5f * scaleY),
                            end = Offset(12f * scaleX, 19f * scaleY),
                            strokeWidth = 2f * density,
                            cap = StrokeCap.Round
                        )
                        drawLine(
                            color = iconColor,
                            start = Offset(5f * scaleX, 12f * scaleY),
                            end = Offset(19f * scaleX, 12f * scaleY),
                            strokeWidth = 2f * density,
                            cap = StrokeCap.Round
                        )
                    } else {
                        // Car outline path: d="M5 13l1-2.8A1.5 1.5 0 0 1 7.4 9.2h9.2a1.5 1.5 0 0 1 1.4 1l1 2.8M6 13h12v3M6 13v3"
                        val p = Path().apply {
                            moveTo(5f * scaleX, 13f * scaleY)
                            lineTo(6f * scaleX, 10.2f * scaleY)
                            // Bezier curve equivalent of Arc: 1.5 A1.5
                            quadraticTo(
                                6.5f * scaleX, 9.2f * scaleY,
                                7.4f * scaleX, 9.2f * scaleY
                            )
                            lineTo(16.6f * scaleX, 9.2f * scaleY)
                            quadraticTo(
                                17.5f * scaleX, 9.2f * scaleY,
                                18f * scaleX, 10.2f * scaleY
                            )
                            lineTo(19f * scaleX, 13f * scaleY)
                            
                            moveTo(6f * scaleX, 13f * scaleY)
                            lineTo(18f * scaleX, 13f * scaleY)
                            lineTo(18f * scaleX, 16f * scaleY)
                            
                            moveTo(6f * scaleX, 13f * scaleY)
                            lineTo(6f * scaleX, 16f * scaleY)
                        }
                        drawPath(
                            path = p,
                            color = iconColor,
                            style = Stroke(width = 1.7f * density, cap = StrokeCap.Round)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = transaction.title,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color(0xFFF3F6FA) else Color(0xFF101620)
                )
                
                // Formatted DateTime
                val isToday = transaction.dateTime.toLocalDate() == java.time.LocalDate.now()
                val isYesterday = transaction.dateTime.toLocalDate() == java.time.LocalDate.now().minusDays(1)
                val dayPrefix = when {
                    isToday -> "Bugün"
                    isYesterday -> "Dün"
                    else -> transaction.dateTime.format(DateTimeFormatter.ofPattern("d MMM", Locale("tr")))
                }
                val timeString = transaction.dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
                Text(
                    text = "$dayPrefix · $timeString",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isDark) Color(0xFF7A828F) else Color(0xFF8A929E)
                )
            }

            // Amount
            val prefix = if (isPositive) "+" else "−"
            val formattedAmount = String.format(Locale("tr", "TR"), "%.2f", Math.abs(transaction.amount))
            Text(
                text = "$prefix₺$formattedAmount",
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isPositive) {
                    if (isDark) Color(0xFF34C98A) else Color(0xFF1A9E63)
                } else {
                    if (isDark) Color(0xFFF3F6FA) else Color(0xFF101620)
                }
            )
        }

        if (!isLast) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(color = if (isDark) Color(0xFF232A33) else Color(0xFFF0F2F6))
            )
        }
    }
}

@Composable
fun AddBalanceDialog(
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var customAmount by remember { mutableStateOf("") }
    val options = listOf(50.0, 100.0, 200.0, 500.0)

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                text = "Bakiye Yükle",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column {
                Text(
                    text = "Yüklemek istediğiniz bakiye miktarını seçin veya girin:",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                // Fast options grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    options.forEach { amount ->
                        Button(
                            onClick = { onConfirm(amount) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "₺${amount.toInt()}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Custom amount text field
                OutlinedTextField(
                    value = customAmount,
                    onValueChange = { customAmount = it },
                    label = { Text("Farklı Miktar (₺)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = customAmount.toDoubleOrNull()
                    if (amount != null && amount > 0) {
                        onConfirm(amount)
                    }
                },
                enabled = customAmount.isNotEmpty() && customAmount.toDoubleOrNull() != null,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Onayla", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Vazgeç", fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun AddCardDialog(
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (brand: String, last4: String, expMonth: Int, expYear: Int) -> Unit
) {
    var brand by remember { mutableStateOf("MASTERCARD") }
    var last4 by remember { mutableStateOf("") }
    var expMonth by remember { mutableStateOf("") }
    var expYear by remember { mutableStateOf("") }

    val monthNumber = expMonth.toIntOrNull()
    val yearNumber = expYear.toIntOrNull()
    val isValid = brand.isNotBlank() &&
        last4.length == 4 &&
        last4.all { it.isDigit() } &&
        monthNumber != null &&
        monthNumber in 1..12 &&
        yearNumber != null &&
        yearNumber >= 2026

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                text = "Kart Ekle",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Kart listesi yalnız marka, son 4 hane ve son kullanma tarihini kaydeder.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { brand = "VISA" },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (brand == "VISA") Color(0xFF0B6BCB) else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (brand == "VISA") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("VISA", fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { brand = "MASTERCARD" },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (brand == "MASTERCARD") Color(0xFF0B6BCB) else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (brand == "MASTERCARD") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("MC", fontWeight = FontWeight.Bold)
                    }
                }

                OutlinedTextField(
                    value = last4,
                    onValueChange = { value -> last4 = value.filter { it.isDigit() }.take(4) },
                    label = { Text("Son 4 hane") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = expMonth,
                        onValueChange = { value -> expMonth = value.filter { it.isDigit() }.take(2) },
                        label = { Text("Ay") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = expYear,
                        onValueChange = { value -> expYear = value.filter { it.isDigit() }.take(4) },
                        label = { Text("Yıl") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    )
                }

                TextButton(
                    onClick = {
                        brand = "MASTERCARD"
                        last4 = "0008"
                        expMonth = "12"
                        expYear = "2030"
                    }
                ) {
                    Text("Test kartını doldur")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(brand, last4, monthNumber ?: 12, yearNumber ?: 2030) },
                enabled = isValid && !isLoading,
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    Text("Ekle", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isLoading) {
                Text("Vazgeç", fontWeight = FontWeight.Bold)
            }
        }
    )
}
