package com.turkcell.rencar.feature.rentals.presentation.history

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.turkcell.rencar.feature.rentals.domain.model.Rental
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RentalHistoryScreen(
    modifier: Modifier = Modifier,
    viewModel: RentalHistoryViewModel = hiltViewModel(),
    onNavigateToDetail: (String) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is RentalHistoryEffect.ShowSnackbar -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                is RentalHistoryEffect.NavigateToDetail -> {
                    onNavigateToDetail(effect.rentalId)
                }
            }
        }
    }

    val isDark = MaterialTheme.colorScheme.background == Color(0xFF121212) // Check dark theme
    val backgroundColor = if (isDark) Color(0xFF0C0F14) else Color(0xFFF4F6F9)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp)
            ) {
                // Header
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Kiralamalarım",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color(0xFFF3F6FA) else Color(0xFF101620),
                    letterSpacing = (-0.5).sp
                )
                Spacer(modifier = Modifier.height(3.dp))
                
                val formattedTotalSpend = String.format(Locale("tr", "TR"), "%.2f", state.totalSpend)
                Text(
                    text = "Bu ay ${state.totalTripsCount} yolculuk · ₺$formattedTotalSpend harcama",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isDark) Color(0xFF98A2B0) else Color(0xFF5C6675)
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Rentals List
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.rentals) { rental ->
                        RentalHistoryItem(
                            rental = rental,
                            isDark = isDark,
                            onClick = {
                                viewModel.onEvent(RentalHistoryEvent.RefreshRentals)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RentalHistoryItem(
    rental: Rental,
    isDark: Boolean,
    onClick: () -> Unit
) {
    val cardBackground = if (isDark) Color(0xFF171C24) else Color(0xFFFFFFFF)
    val cardBorderModifier = if (isDark) {
        Modifier.border(1.dp, Color(0xFF232A33), RoundedCornerShape(20.dp))
    } else {
        Modifier.shadow(elevation = 2.dp, shape = RoundedCornerShape(20.dp), clip = false)
    }

    Surface(
        onClick = onClick,
        color = cardBackground,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .then(cardBorderModifier)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Path Drawing Column
            RentalPathPreview(rentalId = rental.id, isDark = isDark)
            
            Spacer(modifier = Modifier.width(13.dp))
            
            // Detail Column
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "${rental.vehicle.brand} ${rental.vehicle.model}",
                        fontSize = 15.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color(0xFFF3F6FA) else Color(0xFF101620)
                    )

                    val formattedPrice = String.format(Locale("tr", "TR"), "%.2f", rental.totalPrice ?: 0.0)
                    Text(
                        text = "₺$formattedPrice",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isDark) Color(0xFFF3F6FA) else Color(0xFF101620)
                    )
                }
                
                Spacer(modifier = Modifier.height(3.dp))
                
                // Formatted Date
                val formatter = DateTimeFormatter.ofPattern("d MMM yyyy · HH:mm", Locale("tr", "TR"))
                val dateText = (rental.endedAt ?: rental.startedAt ?: rental.createdAt).format(formatter)
                Text(
                    text = dateText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isDark) Color(0xFF7A828F) else Color(0xFF8A929E)
                )
                
                Spacer(modifier = Modifier.height(9.dp))
                
                // Duration & Distance Badges
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BadgeTag(text = "${rental.durationMinutes} dk", isDark = isDark)
                    val formattedDist = String.format(Locale("tr", "TR"), "%.1f", rental.distanceKm)
                    BadgeTag(text = "$formattedDist km", isDark = isDark)
                }
            }
        }
    }
}

@Composable
fun BadgeTag(
    text: String,
    isDark: Boolean
) {
    Box(
        modifier = Modifier
            .background(
                color = if (isDark) Color(0xFF222A33) else Color(0xFFF1F4F8),
                shape = RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            color = if (isDark) Color(0xFFB6BFCB) else Color(0xFF5C6675),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun RentalPathPreview(
    rentalId: String,
    isDark: Boolean
) {
    val density = LocalDensity.current.density
    val boxBackground = if (isDark) Color(0xFF11161D) else Color(0xFFE6EBF1)
    val streetColor = if (isDark) Color(0xFF222A33) else Color(0xFFFFFFFF)
    val pathColor = if (isDark) Color(0xFF4C95F0) else Color(0xFF0B6BCB)
    val greenColor = if (isDark) Color(0xFF34C98A) else Color(0xFF1FB370)

    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(boxBackground)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Draw matching custom streets and paths depending on the rental ID to represent HTML SVGs
            when (rentalId) {
                "1" -> {
                    // Clio 1 SVG
                    // Street 1: M-5 24 L70 16
                    drawLine(
                        color = streetColor,
                        start = Offset(-5f * density, 24f * density),
                        end = Offset(70f * density, 16f * density),
                        strokeWidth = 6f * density
                    )
                    // Street 2: M22 -5 L28 70
                    drawLine(
                        color = streetColor,
                        start = Offset(22f * density, -5f * density),
                        end = Offset(28f * density, 70f * density),
                        strokeWidth = 5f * density
                    )
                    // Path: M10 50 Q30 38 40 28 T56 14
                    val path = Path().apply {
                        moveTo(10f * density, 50f * density)
                        quadraticTo(
                            30f * density, 38f * density,
                            40f * density, 28f * density
                        )
                        // T operator: smooth quadratic to (56, 14).
                        // Mirror control point is (50, 18)
                        quadraticTo(
                            50f * density, 18f * density,
                            56f * density, 14f * density
                        )
                    }
                    drawPath(
                        path = path,
                        color = pathColor,
                        style = Stroke(width = 2.4f * density, cap = StrokeCap.Round)
                    )
                    // Start Dot
                    drawCircle(color = pathColor, radius = 3f * density, center = Offset(10f * density, 50f * density))
                    // End Dot
                    drawCircle(color = greenColor, radius = 3f * density, center = Offset(56f * density, 14f * density))
                }
                "2" -> {
                    // Egea SVG
                    // Street 1: M-5 40 L70 48
                    drawLine(
                        color = streetColor,
                        start = Offset(-5f * density, 40f * density),
                        end = Offset(70f * density, 48f * density),
                        strokeWidth = 6f * density
                    )
                    // Street 2: M40 -5 L36 70
                    drawLine(
                        color = streetColor,
                        start = Offset(40f * density, -5f * density),
                        end = Offset(36f * density, 70f * density),
                        strokeWidth = 5f * density
                    )
                    // Path: M52 8 Q34 22 28 36 T14 54
                    val path = Path().apply {
                        moveTo(52f * density, 8f * density)
                        quadraticTo(
                            34f * density, 22f * density,
                            28f * density, 36f * density
                        )
                        // T operator: smooth quadratic to (14, 54).
                        // Mirror control point is (22, 50)
                        quadraticTo(
                            22f * density, 50f * density,
                            14f * density, 54f * density
                        )
                    }
                    drawPath(
                        path = path,
                        color = pathColor,
                        style = Stroke(width = 2.4f * density, cap = StrokeCap.Round)
                    )
                    // Start Dot
                    drawCircle(color = pathColor, radius = 3f * density, center = Offset(52f * density, 8f * density))
                    // End Dot
                    drawCircle(color = greenColor, radius = 3f * density, center = Offset(14f * density, 54f * density))
                }
                "3" -> {
                    // Polo SVG
                    // Street 1: M-5 28 L70 32
                    drawLine(
                        color = streetColor,
                        start = Offset(-5f * density, 28f * density),
                        end = Offset(70f * density, 32f * density),
                        strokeWidth = 6f * density
                    )
                    // Street 2: M18 -5 L22 70
                    drawLine(
                        color = streetColor,
                        start = Offset(18f * density, -5f * density),
                        end = Offset(22f * density, 70f * density),
                        strokeWidth = 5f * density
                    )
                    // Path: M8 14 Q28 30 36 40 T54 50
                    val path = Path().apply {
                        moveTo(8f * density, 14f * density)
                        quadraticTo(
                            28f * density, 30f * density,
                            36f * density, 40f * density
                        )
                        // T operator: smooth quadratic to (54, 50).
                        // Mirror control point is (44, 50)
                        quadraticTo(
                            44f * density, 50f * density,
                            54f * density, 50f * density
                        )
                    }
                    drawPath(
                        path = path,
                        color = pathColor,
                        style = Stroke(width = 2.4f * density, cap = StrokeCap.Round)
                    )
                    // Start Dot
                    drawCircle(color = pathColor, radius = 3f * density, center = Offset(8f * density, 14f * density))
                    // End Dot
                    drawCircle(color = greenColor, radius = 3f * density, center = Offset(54f * density, 50f * density))
                }
                else -> {
                    // Clio 4 SVG
                    // Street 1: M-5 20 L70 28
                    drawLine(
                        color = streetColor,
                        start = Offset(-5f * density, 20f * density),
                        end = Offset(70f * density, 28f * density),
                        strokeWidth = 6f * density
                    )
                    // Street 2: M44 -5 L40 70
                    drawLine(
                        color = streetColor,
                        start = Offset(44f * density, -5f * density),
                        end = Offset(40f * density, 70f * density),
                        strokeWidth = 5f * density
                    )
                    // Path: M14 10 Q30 30 34 44 T50 58
                    val path = Path().apply {
                        moveTo(14f * density, 10f * density)
                        quadraticTo(
                            30f * density, 30f * density,
                            34f * density, 44f * density
                        )
                        // T operator: smooth quadratic to (50, 58).
                        // Mirror control point is (38, 58)
                        quadraticTo(
                            38f * density, 58f * density,
                            50f * density, 58f * density
                        )
                    }
                    drawPath(
                        path = path,
                        color = pathColor,
                        style = Stroke(width = 2.4f * density, cap = StrokeCap.Round)
                    )
                    // Start Dot
                    drawCircle(color = pathColor, radius = 3f * density, center = Offset(14f * density, 10f * density))
                    // End Dot
                    drawCircle(color = greenColor, radius = 3f * density, center = Offset(50f * density, 58f * density))
                }
            }
        }
    }
}
