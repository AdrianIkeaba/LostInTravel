package com.ghostdev.location.lostintravel.data.repositories

import com.ghostdev.location.lostintravel.data.models.Location
import com.ghostdev.location.lostintravel.data.models.Result

interface UserRepository {
    suspend fun createUser(
        email: String,
        fullName: String,
        location: Location,
        password: String
    ): Result<String>

    suspend fun login(email: String, password: String): Result<Boolean>

    suspend fun getUserFullName(): Result<Boolean>

    suspend fun logout(token: String): Result<Boolean>
}