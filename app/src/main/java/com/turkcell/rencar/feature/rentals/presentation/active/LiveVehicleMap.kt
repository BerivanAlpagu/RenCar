@file:Suppress("DEPRECATION")

package com.turkcell.rencar.feature.rentals.presentation.active

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.turkcell.rencar.feature.rentals.domain.model.VehicleLocation
import org.maplibre.android.annotations.IconFactory
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.annotations.PolylineOptions
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style

@Composable
fun LiveVehicleMap(
    vehicleLocation: VehicleLocation?,
    routePoints: List<VehicleLocation>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val isDark = isSystemInDarkTheme()

    val mapView = remember {
        val options = org.maplibre.android.maps.MapLibreMapOptions.createFromAttributes(context)
            .textureMode(true) // Compose içindeki gölgeli (shadow) kartlarla üst üste binince SurfaceView modu şeffaf/boş kalıyor; TextureView modu doğru katmanlanıyor.
        MapView(context, options).apply {
            getMapAsync { mapboxMap ->
                mapboxMap.setStyle(Style.Builder().fromUri("https://tiles.basemaps.cartocdn.com/gl/voyager-gl-style/style.json"))
                mapboxMap.uiSettings.isAttributionEnabled = false
                mapboxMap.uiSettings.isLogoEnabled = false
                mapboxMap.uiSettings.isRotateGesturesEnabled = false

                val defaultPos = CameraPosition.Builder()
                    .target(LatLng(41.0369, 28.9850)) // Taksim - araç konumu gelene kadar varsayılan
                    .zoom(14.0)
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

    var isCameraCentered by remember { mutableStateOf(false) }

    val vehicleIcon = remember {
        IconFactory.getInstance(context).fromBitmap(createLiveVehicleBitmap(context))
    }
    val startIcon = remember {
        IconFactory.getInstance(context).fromBitmap(createRouteStartBitmap(context))
    }

    LaunchedEffect(vehicleLocation, routePoints.size) {
        val current = vehicleLocation ?: return@LaunchedEffect
        mapView.getMapAsync { mapboxMap ->
            mapboxMap.clear()

            val start = routePoints.firstOrNull()
            if (start != null && (start.latitude != current.latitude || start.longitude != current.longitude)) {
                mapboxMap.addMarker(
                    MarkerOptions()
                        .position(LatLng(start.latitude, start.longitude))
                        .icon(startIcon)
                        .snippet("route_start")
                )
            }

            if (routePoints.size > 1) {
                mapboxMap.addPolyline(
                    PolylineOptions()
                        .addAll(routePoints.map { LatLng(it.latitude, it.longitude) })
                        .color(Color.parseColor("#0B6BCB"))
                        .width(5f)
                )
            }

            mapboxMap.addMarker(
                MarkerOptions()
                    .position(LatLng(current.latitude, current.longitude))
                    .icon(vehicleIcon)
                    .snippet("vehicle")
            )

            val target = LatLng(current.latitude, current.longitude)
            if (!isCameraCentered) {
                mapboxMap.cameraPosition = CameraPosition.Builder().target(target).zoom(16.0).build()
                isCameraCentered = true
            } else {
                mapboxMap.animateCamera(CameraUpdateFactory.newLatLng(target), 800)
            }
        }
    }

    Box(modifier = modifier) {
        AndroidView(factory = { mapView }, modifier = Modifier.fillMaxSize())

        if (vehicleLocation == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(if (isDark) androidx.compose.ui.graphics.Color(0xCC11161D) else androidx.compose.ui.graphics.Color(0xCCE6EBF1)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .shadow(16.dp, RoundedCornerShape(20.dp))
                        .background(if (isDark) androidx.compose.ui.graphics.Color(0xFF171C24) else androidx.compose.ui.graphics.Color.White, RoundedCornerShape(20.dp))
                        .padding(horizontal = 18.dp, vertical = 12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            color = androidx.compose.ui.graphics.Color(0xFF0B6BCB),
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.size(10.dp))
                        Text(
                            text = "Canlı konum bağlanıyor…",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDark) androidx.compose.ui.graphics.Color(0xFFF3F6FA) else androidx.compose.ui.graphics.Color(0xFF101620)
                        )
                    }
                }
            }
        }
    }
}

private fun createLiveVehicleBitmap(context: Context): Bitmap {
    val scale = context.resources.displayMetrics.density
    val size = (44f * scale).toInt()
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val center = size / 2f
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    paint.color = Color.parseColor("#33000000")
    canvas.drawCircle(center, center, center - (1f * scale), paint)

    paint.color = Color.WHITE
    canvas.drawCircle(center, center, center - (3f * scale), paint)

    paint.color = Color.parseColor("#0B6BCB")
    canvas.drawCircle(center, center, center - (6f * scale), paint)

    paint.color = Color.WHITE
    paint.textSize = 18f * scale
    paint.textAlign = Paint.Align.CENTER
    val metrics = paint.fontMetrics
    val textY = center - (metrics.ascent + metrics.descent) / 2f
    canvas.drawText("🚗", center, textY, paint)

    return bitmap
}

private fun createRouteStartBitmap(context: Context): Bitmap {
    val scale = context.resources.displayMetrics.density
    val size = (18f * scale).toInt()
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    paint.color = Color.WHITE
    canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint)
    paint.color = Color.parseColor("#5C6675")
    canvas.drawCircle(size / 2f, size / 2f, size / 2f - (3f * scale), paint)

    return bitmap
}
