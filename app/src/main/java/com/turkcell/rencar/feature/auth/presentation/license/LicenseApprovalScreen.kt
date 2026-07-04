package com.turkcell.rencar.feature.auth.presentation.license

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun LicenseApprovalScreen(
    viewModel: LicenseViewModel,
    onApproved: () -> Unit,
    onRejected: () -> Unit
) {
    val status by viewModel.status.collectAsState()
    val bg = Color(0xFFF4F6F9)
    val textPrimary = Color(0xFF101620)
    val textSecondary = Color(0xFF5C6675)
    val buttonBlue = Color(0xFF0B6BCB)

    LaunchedEffect(Unit) {
        viewModel.refreshStatus()
        while (true) {
            delay(3500)
            viewModel.refreshStatus()
        }
    }

    LaunchedEffect(status) {
        if (status == "APPROVED") {
            delay(900)
            onApproved()
        }
        if (status == "REJECTED") {
            onRejected()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (status == "APPROVED") "Başvurunuz onaylandı" else "Admin onayı bekleniyor",
                color = textPrimary,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = when (status) {
                    "APPROVED" -> "Birkaç saniye içinde ana ekrana yönlendirileceksiniz."
                    "REJECTED" -> "Başvurunuz reddedildi. Tekrar ehliyet yükleyebilirsiniz."
                    else -> "Ehliyetiniz admin tarafından inceleniyor. Onay gelince otomatik geçiş yapılacak."
                },
                color = textSecondary,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(28.dp))

            if (status == "APPROVED") {
                Button(
                    onClick = onApproved,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = buttonBlue)
                ) {
                    Text("Home'a dön", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                CircularProgressIndicator(color = buttonBlue)
            }
        }
    }
}
