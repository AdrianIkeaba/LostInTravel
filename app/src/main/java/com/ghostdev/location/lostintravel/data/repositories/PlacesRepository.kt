package com.ghostdev.location.lostintravel.data.repositories

import com.ghostdev.location.lostintravel.data.models.Result

interface PlacesRepository {
    suspend fun getRecommendedPlaces(): Result<Boolean>
}