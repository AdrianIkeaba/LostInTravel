package com.ghostdev.location.lostintravel.data.usecase

import com.ghostdev.location.lostintravel.data.models.Location
import com.ghostdev.location.lostintravel.data.models.Result
import com.ghostdev.location.lostintravel.data.repositories.UserRepository
import org.koin.java.KoinJavaComponent.inject

class CreateUserUseCase() {
    private val userRepository by inject<UserRepository>(UserRepository::class.java)
    suspend operator fun invoke(
        email: String,
        fullName: String,
        latitude: String,
        longitude: String,
        password: String
    ): Result<String> {
        val location = Location(latitude, longitude)
        return userRepository.createUser(email, fullName, location, password)
    }
}