package com.turkcell.rencar.feature.rentals.data.remote.socket

import com.turkcell.rencar.app.di.ApiConfig
import com.turkcell.rencar.feature.auth.data.local.TokenManager
import com.turkcell.rencar.feature.rentals.domain.model.VehicleLocation
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

private const val LOCATIONS_NAMESPACE = "ws/locations"

@Singleton
class VehicleLocationSocketClient @Inject constructor(
    private val tokenManager: TokenManager
) {

    fun observeMyVehicle(): Flow<VehicleLocation> = callbackFlow {
        val token = tokenManager.accessToken.first()
        if (token.isNullOrBlank()) {
            close()
            return@callbackFlow
        }

        val options = IO.Options().apply {
            extraHeaders = mapOf("Authorization" to listOf("Bearer $token"))
            reconnection = true
        }

        val socket = IO.socket("${ApiConfig.BASE_URL.trimEnd('/')}/$LOCATIONS_NAMESPACE", options)

        val listener = Emitter.Listener { args ->
            val payload = args.getOrNull(0) as? JSONObject ?: return@Listener
            val vehicle = payload.optJSONObject("vehicle") ?: return@Listener
            val vehicleId = vehicle.optString("vehicleId")
            if (vehicleId.isBlank()) return@Listener
            trySend(
                VehicleLocation(
                    vehicleId = vehicleId,
                    latitude = vehicle.optDouble("latitude"),
                    longitude = vehicle.optDouble("longitude"),
                    updatedAt = vehicle.optString("updatedAt").ifBlank { null }
                )
            )
        }

        socket.on("my-vehicle", listener)
        socket.on(Socket.EVENT_CONNECT_ERROR) { /* sessizce yut, REST polling akışı ekranı çalışır durumda tutar */ }
        socket.connect()

        awaitClose {
            socket.off("my-vehicle", listener)
            socket.disconnect()
            socket.close()
        }
    }
}
