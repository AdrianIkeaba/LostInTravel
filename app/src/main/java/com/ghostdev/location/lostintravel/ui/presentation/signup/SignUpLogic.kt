package com.ghostdev.location.lostintravel.ui.presentation.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ghostdev.location.lostintravel.data.models.LocationData
import com.ghostdev.location.lostintravel.data.usecase.CreateUserUseCase
import com.ghostdev.location.lostintravel.data.service.LocationResult
import com.ghostdev.location.lostintravel.data.service.LocationService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
import com.ghostdev.location.lostintravel.data.models.Result

class SignUpLogic() : ViewModel() {
    private val locationService by inject<LocationService>(LocationService::class.java)
    private val createUserUseCase by inject<CreateUserUseCase>(CreateUserUseCase::class.java)

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    init {
        fetchCurrentLocation()
    }

    fun fetchCurrentLocation() {
        viewModelScope.launch {
            locationService.getCurrentLocation().collect { result ->
                when (result) {
                    is LocationResult.Loading -> {
                        _uiState.update { it.copy(isLocationLoading = true) }
                    }
                    is LocationResult.Success -> {
                        _uiState.update {
                            it.copy(
                                isLocationLoading = false,
                                locationData = result.locationData,
                                locationDisplay = result.locationData.displayName
                            )
                        }
                        println("Location updated: ${result.locationData.displayName}")
                    }
                    is LocationResult.Error -> {
                        _uiState.update {
                            it.copy(
                                isLocationLoading = false,
                                locationError = result.message,
                                locationDisplay = "Location unavailable"
                            )
                        }
                        delay(5000)
                        clearError()
                    }
                }
            }
        }
    }

    fun updateEmail(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun updateFullName(fullName: String) {
        _uiState.update { it.copy(fullName = fullName) }
    }

    fun updatePassword(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun createUser() {
        _uiState.update { it.copy(isLoading = true, error = null) }

        val currentState = _uiState.value

        if (currentState.locationData == null) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    error = "Location data is required"
                )
            }
            viewModelScope.launch {
                delay(5000)
                clearError()
            }
            return
        }

        viewModelScope.launch {
            try {
                val result = createUserUseCase(
                    email = currentState.email,
                    fullName = currentState.fullName,
                    latitude = currentState.locationData.latitude,
                    longitude = currentState.locationData.longitude,
                    password = currentState.password
                )

                when (result) {
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

    private fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class SignUpUiState(
    val email: String = "",
    val fullName: String = "",
    val password: String = "",
    val locationData: LocationData? = null,
    val locationDisplay: String = "Fetching location...",
    val isLocationLoading: Boolean = false,
    val locationError: String? = null,
    val isLoading: Boolean = false,
    val isSuccessful: Boolean = false,
    val error: String? = null
)