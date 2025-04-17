package com.ghostdev.location.lostintravel.data.usecase

import com.ghostdev.location.lostintravel.data.models.Result
import com.ghostdev.location.lostintravel.data.repositories.UserRepository
import org.koin.java.KoinJavaComponent.inject
import kotlin.getValue

class GetUserFullNameUseCase {
    private val userRepository by inject<UserRepository>(UserRepository::class.java)
    suspend operator fun invoke(): Result<Boolean> {
        return userRepository.getUserFullName()
    }
}