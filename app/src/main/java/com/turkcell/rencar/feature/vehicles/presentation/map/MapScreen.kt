@file:Suppress("DEPRECATION")

package com.turkcell.rencar.feature.vehicles.presentation.map

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.turkcell.rencar.feature.vehicles.domain.model.Vehicle
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng

@Composable
fun MapScreen(
    viewModel: MapViewModel = hiltViewModel(),
    onUnlockClick: (String) -> Unit = {},
    onResumeHandoverClick: (String) -> Unit = {},
    onResumeActiveRentalClick: (String) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            viewModel.onEvent(MapEvent.OnLocationPermissionGranted)
        } else {
            viewModel.onEvent(MapEvent.OnLocationPermissionDenied)
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is MapEffect.NavigateToUnlock -> onUnlockClick(effect.vehicleId)
                is MapEffect.NavigateToHandoverPhoto -> onResumeHandoverClick(effect.rentalId)
                is MapEffect.NavigateToActiveRental -> onResumeActiveRentalClick(effect.rentalId)
                is MapEffect.ShowError -> {
                    android.widget.Toast.makeText(context, effect.message, android.widget.Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF3F5F8))) {
        // Map Layer
        MapLibreView(
            state = state,
            modifier = Modifier.fillMaxSize(),
            onMapClick = {
                viewModel.onEvent(MapEvent.DismissVehicleDetails)
            },
            onMarkerClick = { vehicle ->
                viewModel.onEvent(MapEvent.OnVehicleClicked(vehicle.id))
            }
        )

        // Top Overlay (Search Bar or Back Button)
        if (state.selectedVehicle == null) {
            TopSearchBarOverlay(
                searchQuery = state.searchQuery,
                onSearchQueryChanged = { viewModel.onEvent(MapEvent.OnSearchQueryChanged(it)) }
            )
        } else {
            FloatingBackButton(
                onClick = { viewModel.onEvent(MapEvent.DismissVehicleDetails) },
                modifier = Modifier.padding(top = 16.dp, start = 18.dp)
            )
        }

        // Loading Indicator
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // Bottom Sheet
        if (state.selectedVehicle != null) {
            val selected = state.selectedVehicle!!
            val isReservedByMe = state.activeReservation?.vehicleId == selected.id
            val isMyActiveRental = state.activeRental?.vehicleId == selected.id
            VehicleDetailBottomSheet(
                vehicle = selected,
                isReservedByMe = isReservedByMe,
                remainingSeconds = state.activeReservation?.remainingSeconds?.takeIf { isReservedByMe },
                isReserving = state.isReserving,
                isMyActiveRental = isMyActiveRental,
                isActiveRentalStarted = state.activeRental?.status == com.turkcell.rencar.feature.rentals.domain.model.RentalStatus.ACTIVE,
                onReserveClick = { viewModel.onEvent(MapEvent.OnReserveClicked(selected.id)) },
                onUnlockClick = { viewModel.onEvent(MapEvent.OnUnlockClicked(selected.id)) },
                onResumeClick = { viewModel.onEvent(MapEvent.OnResumeRentalClicked(selected.id)) },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        } else {
            BottomVehicleSheet(
                vehicleCount = state.vehicles.filter { 
                    it.brand.contains(state.searchQuery, true) || it.model.contains(state.searchQuery, true) || it.type.contains(state.searchQuery, true)
                }.size,
                locationText = state.locationText,
                selectedFilter = state.selectedFilter,
                onFilterChanged = { viewModel.onEvent(MapEvent.OnFilterChanged(it)) },
                onFindNearestClicked = {
                    val filtered = state.vehicles.filter { 
                        it.brand.contains(state.searchQuery, true) || it.model.contains(state.searchQuery, true) || it.type.contains(state.searchQuery, true)
                    }
                    if (filtered.isNotEmpty() && state.userLocation != null) {
                        val nearest = filtered.minByOrNull { vehicle ->
                            val results = FloatArray(1)
                            android.location.Location.distanceBetween(
                                state.userLocation!!.latitude, state.userLocation!!.longitude,
                                vehicle.latitude, vehicle.longitude, results
                            )
                            results[0]
                        }
                        if (nearest != null) {
                            viewModel.onEvent(MapEvent.OnVehicleClicked(nearest.id))
                        }
                    }
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
fun FloatingBackButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(42.dp)
            .shadow(16.dp, RoundedCornerShape(13.dp), spotColor = Color(0x33101828))
            .background(Color.White, RoundedCornerShape(13.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = Color(0xFF141A22),
            modifier = Modifier.size(26.dp)
        )
    }
}

@Composable
fun VehicleDetailBottomSheet(
    vehicle: Vehicle,
    isReservedByMe: Boolean = false,
    remainingSeconds: Long? = null,
    isReserving: Boolean = false,
    isMyActiveRental: Boolean = false,
    isActiveRentalStarted: Boolean = false,
    onReserveClick: () -> Unit = {},
    onUnlockClick: () -> Unit = {},
    onResumeClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(40.dp, RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp), spotColor = Color(0x33101828))
            .background(Color.White, RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
            .padding(top = 14.dp, start = 22.dp, end = 22.dp, bottom = 24.dp) // Removed extra bottom padding, 24dp for safe area
    ) {
        // Drag handle
        Box(
            modifier = Modifier
                .width(42.dp)
                .height(5.dp)
                .background(Color(0xFFE0E5EC), RoundedCornerShape(3.dp))
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(14.dp))

        // Title and Status
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${vehicle.brand} ${vehicle.model}",
                fontSize = 21.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF101620),
                letterSpacing = (-0.4).sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            val statusLabel = when {
                isMyActiveRental && isActiveRentalStarted -> "SÜRÜŞÜN AKTİF"
                isMyActiveRental -> "FOTOĞRAF BEKLİYOR"
                isReservedByMe -> "REZERVE EDİLDİ"
                vehicle.status == "AVAILABLE" -> "MÜSAİT"
                else -> "DOLU"
            }
            val statusHighlighted = isMyActiveRental || isReservedByMe || vehicle.status == "AVAILABLE"
            val statusBg = if (statusHighlighted) Color(0xFFE7F4EC) else Color(0xFFF1F4F8)
            val statusColor = if (statusHighlighted) Color(0xFF1A9E63) else Color(0xFF5C6675)
            Box(
                modifier = Modifier
                    .background(statusBg, RoundedCornerShape(7.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = statusLabel,
                    color = statusColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = "${vehicle.plate} · 250 m uzaklıkta",
            fontSize = 13.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF5C6675),
            letterSpacing = 0.5.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Car Image Placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(128.dp)
                .background(Color(0xFFF3F5F8), RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("Araç fotoğrafı", color = Color(0xFF8A929E), fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Info Cards Row 1
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            // Fuel
            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(Color(0xFFF4F6F9), RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                Text("Yakıt", fontSize = 11.5.sp, color = Color(0xFF5C6675), fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(5.dp))
                Text("%${vehicle.fuelPercent.toInt()}", fontSize = 17.sp, color = Color(0xFF101620), fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(7.dp))
                Box(modifier = Modifier.fillMaxWidth().height(5.dp).background(Color(0xFFE3E8EF), RoundedCornerShape(2.5.dp))) {
                    Box(modifier = Modifier.fillMaxWidth((vehicle.fuelPercent / 100.0).toFloat().coerceIn(0f, 1f)).fillMaxHeight().background(Color(0xFF1FB370), RoundedCornerShape(2.5.dp)))
                }
            }
            // Range
            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(Color(0xFFF4F6F9), RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                Text("Menzil", fontSize = 11.5.sp, color = Color(0xFF5C6675), fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(5.dp))
                Text("~${vehicle.rangeKm.toInt()} km", fontSize = 17.sp, color = Color(0xFF101620), fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(9.dp))
                Text("Tahmini menzil", fontSize = 11.sp, color = Color(0xFF8A929E), fontWeight = FontWeight.Medium)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Info Cards Row 2
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            // Transmission
            Row(
                modifier = Modifier
                    .weight(1f)
                    .background(Color(0xFFF4F6F9), RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp, vertical = 11.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Vites", fontSize = 11.sp, color = Color(0xFF8A929E), fontWeight = FontWeight.SemiBold)
                    Text(if (vehicle.transmission == "AUTOMATIC") "Otomatik" else "Manuel", fontSize = 13.5.sp, color = Color(0xFF101620), fontWeight = FontWeight.Bold)
                }
            }
            // Seats
            Row(
                modifier = Modifier
                    .weight(1f)
                    .background(Color(0xFFF4F6F9), RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp, vertical = 11.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Koltuk", fontSize = 11.sp, color = Color(0xFF8A929E), fontWeight = FontWeight.SemiBold)
                    Text("${vehicle.seats} kişi", fontSize = 13.5.sp, color = Color(0xFF101620), fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        // Price Row
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text("₺${vehicle.pricePerMinute}", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF101620))
                Text(" /dk", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF5C6675), modifier = Modifier.padding(bottom = 2.dp))
            }
            Text("Günlük ₺${vehicle.pricePerDay}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF5C6675))
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (isReservedByMe && remainingSeconds != null) {
            val minutes = remainingSeconds / 60
            val seconds = remainingSeconds % 60
            Text(
                text = "Rezerve edildi · %d:%02d kaldı".format(minutes, seconds),
                color = Color(0xFF1A9E63),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        if (isMyActiveRental) {
            // Zaten kilidi açılmış / süren bir kiralaman var — akışa kaldığın yerden devam et
            Button(
                onClick = onResumeClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(26.dp, RoundedCornerShape(18.dp), spotColor = Color(0x4D0B6BCB)),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0B6BCB)),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text(
                    if (isActiveRentalStarted) "Sürüşe Devam Et" else "Fotoğraflara Devam Et",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.5.sp
                )
            }
        } else {
            val canReserve = !isReservedByMe && vehicle.status == "AVAILABLE"
            val canUnlock = isReservedByMe

            // Buttons
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(11.dp)) {
                // Reserve
                Box(
                    modifier = Modifier
                        .weight(0.4f)
                        .height(56.dp)
                        .background(Color.Transparent, RoundedCornerShape(18.dp))
                        .padding(1.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = onReserveClick,
                        enabled = canReserve && !isReserving,
                        modifier = Modifier.fillMaxSize(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color(0xFF0B6BCB)),
                        shape = RoundedCornerShape(18.dp),
                        border = androidx.compose.foundation.BorderStroke(1.7.dp, Color(0xFF0B6BCB))
                    ) {
                        if (isReserving) {
                            CircularProgressIndicator(color = Color(0xFF0B6BCB), strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                        } else {
                            Text("Rezerve Et", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
                // Unlock
                Button(
                    onClick = onUnlockClick,
                    enabled = canUnlock,
                    modifier = Modifier
                        .weight(0.6f)
                        .height(56.dp)
                        .shadow(if (canUnlock) 26.dp else 0.dp, RoundedCornerShape(18.dp), spotColor = Color(0x4D0B6BCB)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0B6BCB),
                        disabledContainerColor = Color(0xFFE3E8EF)
                    ),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text("Kilidi Aç", fontWeight = FontWeight.Bold, fontSize = 15.5.sp)
                }
            }
        }
    }
}

@Composable
fun TopSearchBarOverlay(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 18.dp, end = 18.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .shadow(24.dp, RoundedCornerShape(18.dp), spotColor = Color(0x1F101828))
                .background(Color.White, RoundedCornerShape(18.dp))
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color(0xFF5C6675),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Box(modifier = Modifier.weight(1f)) {
                androidx.compose.foundation.text.BasicTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChanged,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = Color(0xFF101620),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                    singleLine = true,
                    decorationBox = { innerTextField ->
                        Box(contentAlignment = Alignment.CenterStart, modifier = Modifier.fillMaxSize()) {
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = "Araç markası veya modeli ara...",
                                    color = Color(0xFF8A929E),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            }
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(Color(0xFFF1F4F8), RoundedCornerShape(11.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = Color(0xFF3A4452),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun BottomVehicleSheet(
    vehicleCount: Int,
    locationText: String,
    selectedFilter: String?,
    onFilterChanged: (String?) -> Unit,
    onFindNearestClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(30.dp, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp), spotColor = Color(0x1A101828))
            .background(Color.White, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .padding(top = 14.dp, start = 22.dp, end = 22.dp, bottom = 16.dp)
    ) {
        // Drag handle
        Box(
            modifier = Modifier
                .width(42.dp)
                .height(5.dp)
                .background(Color(0xFFE0E5EC), RoundedCornerShape(3.dp))
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Yakınında $vehicleCount araç",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF101620)
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "$locationText çevresinde · 3 dk uzaklıkta",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF5C6675)
                )
            }
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(Color(0xFFF1F4F8), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Filter",
                    tint = Color(0xFF3A4452)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Filters
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterBadge(text = "Tümü", isSelected = selectedFilter == null, color = null) { onFilterChanged(null) }
            FilterBadge(text = "Ekonomik", isSelected = selectedFilter == "HATCHBACK", color = Color(0xFFF5821F)) { onFilterChanged("HATCHBACK") }
            FilterBadge(text = "Konfor", isSelected = selectedFilter == "SEDAN", color = Color(0xFF7C5CE6)) { onFilterChanged("SEDAN") }
            FilterBadge(text = "SUV", isSelected = selectedFilter == "SUV", color = Color(0xFFE6A700)) { onFilterChanged("SUV") }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action Button
        Button(
            onClick = onFindNearestClicked,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .shadow(26.dp, RoundedCornerShape(18.dp), spotColor = Color(0x4D0B6BCB)),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0B6BCB)),
            shape = RoundedCornerShape(18.dp)
        ) {
            Text(
                text = "En Yakın Aracı Bul",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun FilterBadge(text: String, isSelected: Boolean, color: Color?, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .background(
                if (isSelected) Color(0xFF0B6BCB) else Color(0xFFF1F4F8),
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 13.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (color != null) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(color, CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
        }
        Text(
            text = text,
            color = if (isSelected) Color.White else Color(0xFF3A4452),
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
        )
    }
}

@Composable
fun MapLibreView(
    state: MapState,
    modifier: Modifier = Modifier,
    onMapClick: () -> Unit,
    onMarkerClick: (Vehicle) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val mapView = remember {
        org.maplibre.android.maps.MapView(context).apply {
            getMapAsync { mapboxMap ->
                mapboxMap.setStyle(org.maplibre.android.maps.Style.Builder().fromUri("https://tiles.basemaps.cartocdn.com/gl/voyager-gl-style/style.json"))
                mapboxMap.uiSettings.isAttributionEnabled = false
                mapboxMap.uiSettings.isLogoEnabled = false
                
                mapboxMap.addOnMapClickListener {
                    // onMapClick functionality to dismiss vehicle details
                    onMapClick()
                    true
                }
                
                // Fallback default camera position to avoid rendering the entire world map (Zoom 0) 
                // which causes severe lagging and freezing on Android Emulators running on Apple Silicon.
                val defaultPos = CameraPosition.Builder()
                    .target(LatLng(41.0369, 28.9850)) // Taksim
                    .zoom(12.0)
                    .build()
                mapboxMap.cameraPosition = defaultPos
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

    Box(modifier = modifier) {
        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxSize()
        )

        if (state.userLocation != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 240.dp, end = 16.dp)
                    .size(48.dp)
                    .shadow(12.dp, CircleShape, spotColor = Color(0x33000000))
                    .background(Color.White, CircleShape)
                    .clickable {
                        mapView.getMapAsync { map ->
                            val loc = state.userLocation
                            val pos = CameraPosition.Builder()
                                .target(LatLng(loc.latitude, loc.longitude))
                                .zoom(14.0)
                                .build()
                            map.animateCamera(org.maplibre.android.camera.CameraUpdateFactory.newCameraPosition(pos), 800)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.MyLocation, "Center Map", tint = Color(0xFF5C6675), modifier = Modifier.size(24.dp))
            }
        }

        // Zoom Buttons
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .shadow(12.dp, RoundedCornerShape(12.dp), spotColor = Color(0x33000000))
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .clickable {
                        mapView.getMapAsync { map ->
                            val currentZoom = map.cameraPosition.zoom
                            map.animateCamera(org.maplibre.android.camera.CameraUpdateFactory.zoomTo(currentZoom + 1.5), 300)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text("+", fontSize = 24.sp, fontWeight = FontWeight.Medium, color = Color(0xFF3A4452))
            }
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .shadow(12.dp, RoundedCornerShape(12.dp), spotColor = Color(0x33000000))
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .clickable {
                        mapView.getMapAsync { map ->
                            val currentZoom = map.cameraPosition.zoom
                            map.animateCamera(org.maplibre.android.camera.CameraUpdateFactory.zoomTo(currentZoom - 1.5), 300)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text("-", fontSize = 28.sp, fontWeight = FontWeight.Medium, color = Color(0xFF3A4452), modifier = Modifier.padding(bottom = 2.dp))
            }
        }
    }

    var isMapCentered by remember { mutableStateOf(false) }

    // Cache icons for performance
    val locIcon = remember {
        val locBitmap = createUserLocationBitmap(context)
        org.maplibre.android.annotations.IconFactory.getInstance(context).fromBitmap(locBitmap)
    }

    val vehicleIcons = remember(state.vehicles, state.activeReservation, state.activeRental) {
        state.vehicles.associate { vehicle ->
            val isMine = vehicle.id == state.activeReservation?.vehicleId || vehicle.id == state.activeRental?.vehicleId
            val bitmap = createVehicleMarkerBitmap(context, vehicle, isMine)
            val icon = org.maplibre.android.annotations.IconFactory.getInstance(context).fromBitmap(bitmap)
            vehicle.id to icon
        }
    }

    // Update camera for selected vehicle
    LaunchedEffect(state.selectedVehicle) {
        if (state.selectedVehicle != null) {
            mapView.getMapAsync { mapboxMap ->
                val pos = CameraPosition.Builder()
                    .target(LatLng(state.selectedVehicle.latitude, state.selectedVehicle.longitude))
                    .zoom(15.0)
                    .build()
                mapboxMap.animateCamera(org.maplibre.android.camera.CameraUpdateFactory.newCameraPosition(pos), 800)
            }
        }
    }

    // Update camera and markers
    LaunchedEffect(state.vehicles, state.userLocation, state.searchQuery) {
        mapView.getMapAsync { mapboxMap ->
            // Move camera only once when location is found
            if (!isMapCentered && state.userLocation != null) {
                val loc = state.userLocation
                val position = CameraPosition.Builder()
                    .target(LatLng(loc.latitude, loc.longitude))
                    .zoom(14.0)
                    .build()
                mapboxMap.cameraPosition = position
                isMapCentered = true
            }
            
            // Render custom markers for vehicles
            mapboxMap.clear() // clear previous
            
            // Render User Location if available
            if (state.userLocation != null) {
                mapboxMap.addMarker(
                    MarkerOptions()
                        .position(LatLng(state.userLocation.latitude, state.userLocation.longitude))
                        .icon(locIcon)
                        .snippet("user_loc")
                )
            }
            
            val displayedVehicles = state.vehicles.filter {
                it.brand.contains(state.searchQuery, true) || it.model.contains(state.searchQuery, true) || it.type.contains(state.searchQuery, true)
            }

            displayedVehicles.forEach { vehicle ->
                val icon = vehicleIcons[vehicle.id]
                if (icon != null) {
                    mapboxMap.addMarker(
                        MarkerOptions()
                            .position(LatLng(vehicle.latitude, vehicle.longitude))
                            .icon(icon)
                            .snippet(vehicle.id) // Use snippet to store vehicle ID
                    )
                }
            }
            
            // Auto fit bounds for displayed vehicles and user location
            if (isMapCentered) {
                val boundsBuilder = org.maplibre.android.geometry.LatLngBounds.Builder()
                var hasBounds = false
                displayedVehicles.forEach { vehicle ->
                    boundsBuilder.include(LatLng(vehicle.latitude, vehicle.longitude))
                    hasBounds = true
                }
                if (state.userLocation != null && displayedVehicles.isNotEmpty()) {
                    boundsBuilder.include(LatLng(state.userLocation!!.latitude, state.userLocation!!.longitude))
                }
                
                if (hasBounds) {
                    try {
                        val bounds = boundsBuilder.build()
                        val padding = 150
                        mapboxMap.animateCamera(org.maplibre.android.camera.CameraUpdateFactory.newLatLngBounds(bounds, padding, padding, padding, padding + 400), 800)
                    } catch (e: Exception) {
                        // Ignore if layout hasn't happened yet
                    }
                }
            }
            
            // Set marker click listener
            mapboxMap.setOnMarkerClickListener { marker ->
                if (marker.snippet == "user_loc") return@setOnMarkerClickListener true
                
                val clickedVehicle = state.vehicles.find { it.id == marker.snippet }
                if (clickedVehicle != null) {
                    onMarkerClick(clickedVehicle)
                }
                true // Return true to indicate we handled the click (prevents info window from showing)
            }
        }
    }
}
