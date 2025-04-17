package com.ghostdev.location.lostintravel.data.remote

import android.content.Context
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloResponse
import com.ghostdev.location.CreateNewUserMutation
import com.ghostdev.location.GetUserQuery
import com.ghostdev.location.LogOutMutation
import com.ghostdev.location.LoginMutation
import com.ghostdev.location.lostintravel.data.models.Location
import com.ghostdev.location.lostintravel.utils.dataStore
import com.ghostdev.location.lostintravel.utils.getApiTokenFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class UserRemoteDataSource(private val context: Context) {
    private val apolloClient = ApolloClient.Builder()
        .serverUrl("https://lostapi.frontendlabs.co.uk/graphql")
        .build()

    private suspend fun getAuthenticatedClient(): ApolloClient {
        val accessToken = context.dataStore.getApiTokenFlow().first() ?: ""
        return ApolloClient.Builder()
            .serverUrl("https://lostapi.frontendlabs.co.uk/graphql")
            .addHttpHeader("Authorization", accessToken)
            .build()
    }

    suspend fun createUser(
        email: String,
        fullName: String,
        location: Location,
        password: String
    ): ApolloResponse<CreateNewUserMutation.Data> = withContext(Dispatchers.IO) {
        val mutation = CreateNewUserMutation(
            email = email,
            fullName = fullName,
            latitude = location.latitude,
            longitude = location.longitude,
            password = password
        )
        apolloClient.mutation(mutation).execute()
    }

    suspend fun login(
        email: String,
        password: String
    ): ApolloResponse<LoginMutation.Data> = withContext(Dispatchers.IO) {
        val mutation = LoginMutation(
            email = email,
            password = password
        )
        apolloClient.mutation(mutation).execute()
    }

    suspend fun getUserFullName() : ApolloResponse<GetUserQuery.Data> = withContext(Dispatchers.IO) {
        val query = GetUserQuery()
        getAuthenticatedClient().query(query).execute()
    }

    suspend fun logout(
        token: String
    ) : ApolloResponse<LogOutMutation.Data> = withContext(Dispatchers.IO) {
        val mutation = LogOutMutation(
            token = token
        )
        apolloClient.mutation(mutation).execute()
    }
}