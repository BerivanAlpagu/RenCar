package com.turkcell.rencar.core.ui

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Araç kiralama akışında (teslim alma / teslim etme) 4 yönlü foto çekim ızgarası.
 * Yükleme/kaydetme davranışı çağırana bırakılır — bu composable yalnız yakalama UI'sini sağlar.
 */
@Composable
fun <T> PhotoCaptureGrid(
    sides: List<T>,
    photos: Map<T, Bitmap?>,
    label: (T) -> String,
    isDark: Boolean,
    onSideClick: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(sides) { side ->
            PhotoCaptureCard(
                label = label(side),
                bitmap = photos[side],
                isDark = isDark,
                onClick = { onSideClick(side) }
            )
        }
    }
}

@Composable
private fun PhotoCaptureCard(label: String, bitmap: Bitmap?, isDark: Boolean, onClick: () -> Unit) {
    val cardBg = if (isDark) Color(0xFF171C24) else Color.White
    val borderColor = if (isDark) Color(0xFF2C333D) else Color(0xFFC7D0DB)
    val isTaken = bitmap != null

    Box(
        modifier = Modifier
            .height(158.dp)
            .background(if (isTaken) (if (isDark) Color(0xFF11161D) else Color(0xFFE6EBF1)) else cardBg, RoundedCornerShape(18.dp))
            .then(if (!isTaken) Modifier.border(2.dp, borderColor, RoundedCornerShape(18.dp)) else Modifier)
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isTaken) {
            Image(bitmap = bitmap!!.asImageBitmap(), contentDescription = "Photo", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            Box(modifier = Modifier.fillMaxSize().background(Color(0x66000000)))

            Box(modifier = Modifier.align(Alignment.TopStart).padding(8.dp).background(if (isDark) Color.Black else Color(0xFF101620), RoundedCornerShape(7.dp)).padding(horizontal = 9.dp, vertical = 3.dp)) {
                Text(label, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Box(modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).size(24.dp).background(Color(0xFF1FB370), CircleShape), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Check, contentDescription = "Done", tint = Color.White, modifier = Modifier.size(16.dp))
            }
        } else {
            Box(modifier = Modifier.align(Alignment.TopStart).padding(8.dp).background(if (isDark) Color(0xFF222A33) else Color(0xFFEEF1F5), RoundedCornerShape(7.dp)).padding(horizontal = 9.dp, vertical = 3.dp)) {
                Text(label, color = if (isDark) Color(0xFF98A2B0) else Color(0xFF5C6675), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.size(46.dp).background(Color(0xFF0B6BCB), RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Camera", tint = Color.White, modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.height(9.dp))
                Text("Ekle / Çek", fontSize = 12.sp, color = if (isDark) Color(0xFF98A2B0) else Color(0xFF5C6675), fontWeight = FontWeight.Bold)
            }
        }
    }
}
