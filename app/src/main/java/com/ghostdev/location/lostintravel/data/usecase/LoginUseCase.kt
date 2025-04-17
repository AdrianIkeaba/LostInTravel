package com.ghostdev.location.lostintravel.data.usecase

import com.ghostdev.location.lostintravel.data.models.Result
import com.ghostdev.location.lostintravel.data.repositories.UserRepository
import org.koin.java.KoinJavaComponent.inject
import kotlin.getValue

class LoginUseCase() {
    private val userRepository by inject<UserRepository>(UserRepository::class.java)
    suspend operator fun invoke(email: String, password: String): Result<Boolean> {
        return userRepository.login(email, password)
    }
}