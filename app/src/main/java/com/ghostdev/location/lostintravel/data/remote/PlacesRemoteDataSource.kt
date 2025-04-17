package com.ghostdev.location.lostintravel.data.remote

import android.content.Context
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloResponse
import com.ghostdev.location.RecommendedPlacesQuery
import com.ghostdev.location.lostintravel.utils.dataStore
import com.ghostdev.location.lostintravel.utils.getApiTokenFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class PlacesRemoteDataSource(private val context: Context) {
    private suspend fun getAuthenticatedClient(): ApolloClient {
        val accessToken = context.dataStore.getApiTokenFlow().first() ?: ""
        return ApolloClient.Builder()
            .serverUrl("https://lostapi.frontendlabs.co.uk/graphql")
            .addHttpHeader("Authorization", accessToken)
            .build()
    }

    suspend fun getRecommendedPlaces()
    : ApolloResponse<RecommendedPlacesQuery.Data>
    = withContext(
        Dispatchers.IO
    ) {
        val query = RecommendedPlacesQuery()
        getAuthenticatedClient().query(query).execute()
    }

}