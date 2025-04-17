package com.ghostdev.location.lostintravel.data.repositories

import android.content.Context
import com.ghostdev.location.lostintravel.data.models.Location
import com.ghostdev.location.lostintravel.data.models.Result
import com.ghostdev.location.lostintravel.data.remote.UserRemoteDataSource
import com.ghostdev.location.lostintravel.utils.clearAllData
import com.ghostdev.location.lostintravel.utils.dataStore
import com.ghostdev.location.lostintravel.utils.saveApiToken
import com.ghostdev.location.lostintravel.utils.saveFullName
import org.koin.java.KoinJavaComponent.inject

class UserRepositoryImpl(
    private val context: Context
) : UserRepository {
    private val remoteDataSource by inject<UserRemoteDataSource>(UserRemoteDataSource::class.java)

    override suspend fun createUser(
        email: String,
        fullName: String,
        location: Location,
        password: String
    ): Result<String> {
        return try {
            val response = remoteDataSource.createUser(email, fullName, location, password)

            if (response.hasErrors()) {
                Result.Error(response.errors?.first()?.message ?: "Unknown error occurred")
            } else {
                val id = response.data?.CreateNewUser?._id
                if (id != null) {
                    Result.Success("User created successfully")
                } else {
                    Result.Error("Failed to create user: No data returned")
                }
            }
        } catch (e: Exception) {
            Result.Error("Failed to create user: ${e.localizedMessage}")
        }
    }

    override suspend fun login(email: String, password: String): Result<Boolean> {
        return try {
            val response = remoteDataSource.login(email, password)

            if (response.hasErrors()) {
                Result.Error(response.errors?.first()?.message ?: "Unknown error occurred")
            } else {
                val token = response.data?.Login?.token
                if (token != null) {
                    context.dataStore.saveApiToken(token)
                    Result.Success(true)
                } else {
                    Result.Error("Login failed: No token returned")
                }
            }
        } catch (e: Exception) {
            Result.Error("Login failed: ${e.localizedMessage}")
        }
    }

    override suspend fun getUserFullName(): Result<Boolean> {
        return try {
            val response = remoteDataSource.getUserFullName()

            if (response.hasErrors()) {
                Result.Error(response.errors?.first()?.message ?: "Unknown error occurred")
            } else {
                val fullName = response.data?.GetUser?.full_name
                if (fullName != null) {
                    context.dataStore.saveFullName(fullName)
                    Result.Success(true)
                } else {
                    Result.Error("Get user data failed: No data returned")
                }
            }
        } catch (e: Exception) {
            Result.Error("Get user data failed: ${e.localizedMessage}")
        }
    }

    override suspend fun logout(token: String): Result<Boolean> {
        return try {
            val response = remoteDataSource.logout(token = token)

            if (response.hasErrors()) {
                Result.Error(response.errors?.first()?.message ?: "Unknown error occurred")
            } else {
                context.dataStore.clearAllData()
                Result.Success(true)
            }
        } catch (e: Exception) {
            Result.Error("Logout failed: ${e.localizedMessage}")
        }
    }
}