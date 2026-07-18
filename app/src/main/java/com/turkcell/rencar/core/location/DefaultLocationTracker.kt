package com.turkcell.rencar.core.location

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class DefaultLocationTracker @Inject constructor(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val application: Application
) : LocationTracker {

    override suspend fun getCurrentLocation(): Location? {
        val hasAccessFineLocationPermission = ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasAccessCoarseLocationPermission = ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val locationManager = application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if (!hasAccessCoarseLocationPermission || !hasAccessFineLocationPermission || !isGpsEnabled) {
            return null
        }

        return suspendCancellableCoroutine { cont ->
            fusedLocationProviderClient.lastLocation.apply {
                if (isComplete) {
                    if (isSuccessful) {
                        if (result != null) {
                            cont.resume(result)
                        } else {
                            val mock = Location("mock")
                            mock.latitude = 41.0369
                            mock.longitude = 28.9850
                            cont.resume(mock)
                        }
                    } else {
                        val mock = Location("mock")
                        mock.latitude = 41.0369
                        mock.longitude = 28.9850
                        cont.resume(mock)
                    }
                    return@suspendCancellableCoroutine
                }
                addOnSuccessListener {
                    if (it != null) {
                        cont.resume(it)
                    } else {
                        val mock = Location("mock")
                        mock.latitude = 41.0369
                        mock.longitude = 28.9850
                        cont.resume(mock)
                    }
                }
                addOnFailureListener {
                    val mock = Location("mock")
                    mock.latitude = 41.0369
                    mock.longitude = 28.9850
                    cont.resume(mock)
                }
                addOnCanceledListener {
                    cont.cancel()
                }
            }
        }
    }
}
