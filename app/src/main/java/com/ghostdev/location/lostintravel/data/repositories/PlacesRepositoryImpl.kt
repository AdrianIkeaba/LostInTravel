package com.ghostdev.location.lostintravel.data.repositories

import android.content.Context
import com.ghostdev.location.lostintravel.data.remote.PlacesRemoteDataSource
import org.koin.java.KoinJavaComponent.inject
import com.ghostdev.location.lostintravel.data.models.Result
import com.ghostdev.location.lostintravel.utils.dataStore
import com.ghostdev.location.lostintravel.utils.savePlaces

class PlacesRepositoryImpl(
    private val context: Context
) : PlacesRepository {
    private val remoteDataSource by inject<PlacesRemoteDataSource>(PlacesRemoteDataSource::class.java)

    override suspend fun getRecommendedPlaces(): Result<Boolean> {
        return try {
            val response = remoteDataSource.getRecommendedPlaces()

            if (response.hasErrors()) {
                Result.Error(response.errors?.first()?.message ?: "Unknown error occurred")
            } else {
                val places = response.data?.RecommendedPlaces
                if (places != null) {
                    context.dataStore.savePlaces(places)
                    Result.Success(true)
                } else {
                    Result.Error("Get recommended places failed: No data returned")
                }
            }
        } catch (e: Exception) {
            Result.Error("Get recommended places failed: ${e.localizedMessage}")
        }
    }
}