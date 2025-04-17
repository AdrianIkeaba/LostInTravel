package com.ghostdev.location.lostintravel.data.usecase

import com.ghostdev.location.lostintravel.data.repositories.PlacesRepository
import org.koin.java.KoinJavaComponent.inject
import com.ghostdev.location.lostintravel.data.models.Result

class RecommendedPlacesUseCase {
    private val placesRepository by inject<PlacesRepository>(PlacesRepository::class.java)
    suspend operator fun invoke(): Result<Boolean> {
        return placesRepository.getRecommendedPlaces()
    }
}