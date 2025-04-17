package com.ghostdev.location.lostintravel.ui.presentation.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ghostdev.location.lostintravel.data.models.Result
import com.ghostdev.location.lostintravel.data.usecase.GetUserFullNameUseCase
import com.ghostdev.location.lostintravel.data.usecase.LoginUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class SignInLogic : ViewModel() {
    private val loginUseCase by inject<LoginUseCase>(LoginUseCase::class.java)
    private val getUserFullNameUseCase by inject<GetUserFullNameUseCase>(GetUserFullNameUseCase::class.java)

    private val _uiState = MutableStateFlow(SignInUiState())
    val uiState: StateFlow<SignInUiState> = _uiState.asStateFlow()

    fun updateEmail(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun updatePassword(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun login() {
        _uiState.update { it.copy(isLoading = true, error = null) }

        val currentState = _uiState.value

        viewModelScope.launch {
            try {
                val result = loginUseCase(
                    email = currentState.email,
                    password = currentState.password
                )

                when (result) {
                    is Result.Success -> {
                        getUserFullName()
                    }

                    is Result.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = result.message
                            )
                        }
                        delay(5000)
                        clearError()
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "An error occurred: ${e.localizedMessage}"
                    )
                }
                delay(5000)
                clearError()
            }
        }
    }

    private fun getUserFullName() {
        viewModelScope.launch {
            when (val result = getUserFullNameUseCase()) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSuccessful = true,
                            error = null
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                    delay(5000)
                    clearError()
                }
            }
        }
    }

    private fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class SignInUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isSuccessful: Boolean = false,
    val error: String? = null
)