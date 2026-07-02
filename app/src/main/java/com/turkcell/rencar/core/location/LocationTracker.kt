package com.turkcell.rencar.core.location

import android.location.Location

interface LocationTracker {
    suspend fun getCurrentLocation(): Location?
}
