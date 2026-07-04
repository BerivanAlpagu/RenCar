package com.turkcell.rencar.feature.rentals.presentation.active

import android.animation.ValueAnimator
import android.graphics.Color as AndroidColor
import android.view.animation.LinearInterpolator
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.annotations.PolylineOptions
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.Style

@Composable
fun ActiveRentalScreen(
    vehicleId: String,
    viewModel: ActiveRentalViewModel,
    onFinishRentalClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val isDark = isSystemInDarkTheme()

    LaunchedEffect(vehicleId) {
        viewModel.startRental(vehicleId)
    }

    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize().background(if (isDark) Color(0xFF0C0F14) else Color(0xFFF3F5F8)), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFF0B6BCB))
        }
        return
    }

    val bgColor = if (isDark) Color(0xFF10151B) else Color(0xFFE9EDF2)
    val cardColor = if (isDark) Color(0xFF171C24) else Color(0xFFFFFFFF)
    val textColor = if (isDark) Color(0xFFF3F6FA) else Color(0xFF101620)
    val subTextColor = if (isDark) Color(0xFF98A2B0) else Color(0xFF5C6675)
    
    // Simulate time and distance
    var passedTime by remember { mutableStateOf("00:00:00") }
    var passedDistance by remember { mutableStateOf("0,0 km") }
    var totalCost by remember { mutableStateOf("₺15,00") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = bgColor
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Map Layer
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 260.dp) // Leave space for bottom card
                    .background(if (isDark) Color(0xFF11161D) else Color(0xFFE6EBF1))
            ) {
                ActiveRentalMap(
                    onProgress = { progress ->
                        // 10 second simulation progress (0f to 1f)
                        val totalSeconds = (progress * 24 * 60).toInt() // simulate 24 minutes
                        val mins = totalSeconds / 60
                        val secs = totalSeconds % 60
                        passedTime = String.format("00:%02d:%02d", mins, secs)
                        
                        val distance = progress * 12.4f
                        passedDistance = String.format("%.1f km", distance).replace('.', ',')
                        
                        val cost = 15f + (mins * 4.5f)
                        totalCost = String.format("₺%.2f", cost).replace('.', ',')
                    }
                )
            }

            // Status Badge
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 52.dp)
                    .shadow(20.dp, RoundedCornerShape(30.dp), spotColor = Color(0x66000000))
                    .background(if (isDark) Color.White else Color(0xFF101620), RoundedCornerShape(30.dp))
                    .padding(horizontal = 18.dp, vertical = 9.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).background(Color(0xFF1FB370), CircleShape))
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "Kiralama aktif · Renault Clio",
                        color = if (isDark) Color(0xFF101620) else Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Bottom Info Card
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .shadow(40.dp, RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp), spotColor = Color(0x24101828))
                    .background(cardColor, RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                    .padding(18.dp, 22.dp, 18.dp, 30.dp)
            ) {
                // Drag handle
                Box(
                    modifier = Modifier
                        .size(42.dp, 5.dp)
                        .background(if (isDark) Color(0xFF2C333D) else Color(0xFFE0E5EC), RoundedCornerShape(3.dp))
                        .align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(18.dp))

                // Time
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("Geçen süre", fontSize = 13.sp, color = subTextColor, fontWeight = FontWeight.SemiBold)
                    Text(passedTime, fontSize = 46.sp, color = textColor, fontWeight = FontWeight.ExtraBold, letterSpacing = (-1).sp)
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Stats
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(11.dp)) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(if (isDark) Color(0xFF1F262F) else Color(0xFFF4F6F9), RoundedCornerShape(16.dp))
                            .padding(13.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Anlık ücret", fontSize = 11.5.sp, color = subTextColor, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(totalCost, fontSize = 20.sp, color = Color(0xFF0B6BCB), fontWeight = FontWeight.ExtraBold)
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(if (isDark) Color(0xFF1F262F) else Color(0xFFF4F6F9), RoundedCornerShape(16.dp))
                            .padding(13.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Mesafe", fontSize = 11.5.sp, color = subTextColor, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(passedDistance, fontSize = 20.sp, color = textColor, fontWeight = FontWeight.ExtraBold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Buttons
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(11.dp)) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp)
                            .border(1.7.dp, if (isDark) Color(0xFF2A313B) else Color(0xFFE3E8EF), RoundedCornerShape(16.dp))
                            .clickable { /* Lock/Unlock Action */ },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = "Lock", tint = textColor, modifier = Modifier.size(19.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Kilitle / Aç", fontSize = 14.5.sp, fontWeight = FontWeight.Bold, color = textColor)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp)
                            .shadow(24.dp, RoundedCornerShape(16.dp), spotColor = Color(0x4DE5484D))
                            .background(Color(0xFFE5484D), RoundedCornerShape(16.dp))
                            .clickable { onFinishRentalClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Kiralamayı Bitir", fontSize = 14.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun ActiveRentalMap(onProgress: (Float) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val mapView = remember {
        MapView(context).apply {
            getMapAsync { mapboxMap ->
                val isDark = false // You can pass isDark to choose a dark style if you want
                val styleUrl = if (isDark) "https://tiles.basemaps.cartocdn.com/gl/dark-matter-gl-style/style.json" else "https://tiles.basemaps.cartocdn.com/gl/voyager-gl-style/style.json"
                mapboxMap.setStyle(Style.Builder().fromUri(styleUrl)) { style ->
                    startSimulation(mapboxMap, onProgress)
                }
                mapboxMap.uiSettings.isAttributionEnabled = false
                mapboxMap.uiSettings.isLogoEnabled = false
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> mapView.onCreate(null)
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    AndroidView(factory = { mapView }, modifier = Modifier.fillMaxSize())
}

private fun startSimulation(mapboxMap: MapLibreMap, onProgress: (Float) -> Unit) {
    val start = LatLng(41.0369, 28.9850) // Taksim
    val end = LatLng(41.0256, 28.9741)   // Galata

    // Center camera
    val cameraPos = CameraPosition.Builder()
        .target(start)
        .zoom(14.5)
        .build()
    mapboxMap.cameraPosition = cameraPos

    // Add marker
    val marker = mapboxMap.addMarker(
        MarkerOptions()
            .position(start)
            .title("Renault Clio")
    )

    // Add a polyline to show the path
    val polyline = mapboxMap.addPolyline(
        PolylineOptions()
            .add(start)
            .color(AndroidColor.parseColor("#0B6BCB"))
            .width(6f)
    )

    // Animator for 10 seconds
    val animator = ValueAnimator.ofFloat(0f, 1f)
    animator.duration = 10000 // 10 seconds
    animator.interpolator = LinearInterpolator()

    animator.addUpdateListener { animation ->
        val fraction = animation.animatedFraction
        
        // Interpolate position
        val lat = start.latitude + (end.latitude - start.latitude) * fraction
        val lng = start.longitude + (end.longitude - start.longitude) * fraction
        val currentPos = LatLng(lat, lng)

        marker.position = currentPos

        // Update polyline path to leave a trail
        val currentPoints = polyline.points
        currentPoints.add(currentPos)
        polyline.points = currentPoints

        // Keep camera centered on vehicle
        mapboxMap.moveCamera(CameraUpdateFactory.newLatLng(currentPos))
        
        // Callback for UI updates
        onProgress(fraction)
    }

    animator.start()
}
