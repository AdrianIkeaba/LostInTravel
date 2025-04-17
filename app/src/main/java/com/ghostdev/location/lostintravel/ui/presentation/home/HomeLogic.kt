package com.ghostdev.location.lostintravel.ui.presentation.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ghostdev.location.RecommendedPlacesQuery
import com.ghostdev.location.lostintravel.data.models.Result
import com.ghostdev.location.lostintravel.data.usecase.LogOutUseCase
import com.ghostdev.location.lostintravel.data.usecase.RecommendedPlacesUseCase
import com.ghostdev.location.lostintravel.utils.clearAllData
import com.ghostdev.location.lostintravel.utils.dataStore
import com.ghostdev.location.lostintravel.utils.getApiTokenFlow
import com.ghostdev.location.lostintravel.utils.getFullNameFlow
import com.ghostdev.location.lostintravel.utils.getPlacesFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class HomeLogic : ViewModel() {
    private val recommendedPlacesUseCase by inject<RecommendedPlacesUseCase>(RecommendedPlacesUseCase::class.java)
    private val logoutUseCase by inject<LogOutUseCase>(LogOutUseCase::class.java)

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun retrieveUserFullName(context: Context) {
        viewModelScope.launch {
            val fullName = context.dataStore.getFullNameFlow().first()
            _uiState.update {
                it.copy(fullName = fullName ?: "")
            }
        }
    }

    fun getRecommendedPlaces(context: Context) {
        viewModelScope.launch {
            try {
                val result = recommendedPlacesUseCase()
                when(result) {
                    is Result.Success -> {
                        val places = context.dataStore.getPlacesFlow().first()
                        _uiState.update {
                            it.copy(isLoading = false, places = places)
                        }
                    }

                    is Result.Error -> {
                        _uiState.update {
                            it.copy(isLoading = false, error = result.message)
                        }
                        delay(5000)
                        clearError()
                    }
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = "An error occurred: ${e.localizedMessage}")
                }
                delay(5000)
                clearError()
            }
        }
    }



    fun logout(context: Context) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true)
            }
            val result = logoutUseCase(context.dataStore.getApiTokenFlow().first().toString())
            when(result) {
                is Result.Success -> {
                    context.dataStore.clearAllData()
                    _uiState.update {
                        it.copy(isLoggedOut = true, isLoading = false)
                    }
                }

                is Result.Error -> {
                    _uiState.update {
                        it.copy(error = result.message, isLoading = false)
                    }
                    delay(5000)
                }
            }
        }
    }

    private fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class HomeUiState(
    val isLoading: Boolean = false,
    val isLoggedOut: Boolean = false,
    val fullName: String = "",
    val places: List<RecommendedPlacesQuery.RecommendedPlace> = emptyList(),
    val error: String? = null
)
