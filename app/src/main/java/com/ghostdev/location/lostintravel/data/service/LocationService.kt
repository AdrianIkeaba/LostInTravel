package com.ghostdev.location.lostintravel.data.service

import com.ghostdev.location.lostintravel.data.models.LocationData
import kotlinx.coroutines.flow.Flow

interface LocationService {
    fun getCurrentLocation(): Flow<LocationResult>
}

sealed class LocationResult {
    data class Success(val locationData: LocationData) : LocationResult()
    data class Error(val message: String) : LocationResult()
    object Loading : LocationResult()
}