package com.ghostdev.location.lostintravel.data.service

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.core.app.ActivityCompat
import com.ghostdev.location.lostintravel.data.models.LocationData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume

class LocationServiceImpl(private val context: Context) : LocationService {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    override fun getCurrentLocation(): Flow<LocationResult> = flow {
        emit(LocationResult.Loading)

        val hasPermission = hasLocationPermission()

        if (!hasPermission) {
            emit(LocationResult.Error("Location permission is not granted"))
            return@flow
        }

        try {
            val location = getLastLocationOrCurrent()

            if (location != null) {

                val address = geocodeLocation(location)

                if (address != null) {
                    val locationData = LocationData(
                        latitude = location.latitude.toString(),
                        longitude = location.longitude.toString(),
                        city = address.locality ?: address.adminArea ?: "",
                        state = address.adminArea ?: "",
                        country = address.countryName ?: "",
                        displayName = formattedLocationName(address)
                    )
                    emit(LocationResult.Success(locationData))
                } else {
                    emit(LocationResult.Error("Unable to determine address from location"))
                }
            } else {
                emit(LocationResult.Error("Unable to get current location"))
            }
        } catch (e: Exception) {
            emit(LocationResult.Error("Location error: ${e.localizedMessage}"))
        }
    }


    @SuppressLint("MissingPermission")
    private suspend fun getLastLocationOrCurrent(): Location? {
        return suspendCancellableCoroutine { continuation ->
            if (!hasLocationPermission()) {
                continuation.resume(null)
                return@suspendCancellableCoroutine
            }

            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if (!isGpsEnabled && !isNetworkEnabled) {
                continuation.resume(null)
                return@suspendCancellableCoroutine
            }

            var locationRetrieved = false

            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null && !locationRetrieved) {
                        locationRetrieved = true
                        continuation.resume(location)
                    } else if (!locationRetrieved) {
                        val cancellationToken = CancellationTokenSource()

                        fusedLocationClient.getCurrentLocation(
                            Priority.PRIORITY_HIGH_ACCURACY,
                            cancellationToken.token
                        ).addOnSuccessListener { currentLocation ->
                            if (!locationRetrieved) {
                                locationRetrieved = true
                                if (currentLocation != null) {
                                    continuation.resume(currentLocation)
                                } else {
                                    continuation.resume(null)
                                }
                            }
                        }.addOnFailureListener { e ->
                            if (!locationRetrieved) {
                                locationRetrieved = true
                                continuation.resume(null)
                            }
                        }

                        continuation.invokeOnCancellation {
                            cancellationToken.cancel()
                        }
                    }
                }
                .addOnFailureListener { e ->
                    if (!locationRetrieved) {
                        locationRetrieved = true
                        continuation.resume(null)
                    }
                }
        }
    }

    private suspend fun geocodeLocation(location: Location): Address? {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                suspendCancellableCoroutine { continuation ->
                    geocoder.getFromLocation(location.latitude, location.longitude, 1) { addresses ->
                        if (addresses.isNotEmpty()) {
                            val address = addresses[0]
                            continuation.resume(address)
                        } else {
                            continuation.resume(null)
                        }
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    address
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun formattedLocationName(address: Address): String {
        val isUS = address.countryCode == "US"

        return when {
            isUS -> {
                val state = address.adminArea ?: ""
                val country = address.countryName ?: ""
                if (state.isNotEmpty() && country.isNotEmpty()) {
                    "$state, $country"
                } else {
                    country
                }
            }
            else -> {
                val city = address.locality ?: address.adminArea ?: ""
                val country = address.countryName ?: ""
                if (city.isNotEmpty() && country.isNotEmpty()) {
                    "$city, $country"
                } else {
                    country
                }
            }
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}