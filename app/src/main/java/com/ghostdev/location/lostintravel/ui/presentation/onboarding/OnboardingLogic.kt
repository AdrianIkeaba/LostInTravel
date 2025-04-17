package com.ghostdev.location.lostintravel.ui.presentation.onboarding

import android.content.Context
import androidx.lifecycle.ViewModel
import com.ghostdev.location.lostintravel.utils.dataStore
import com.ghostdev.location.lostintravel.utils.getApiTokenFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class OnboardingLogic() : ViewModel() {

    fun isUserLoggedIn(context: Context): Boolean {
        var isLoggedIn = false
        val tokenFlow = context.dataStore.getApiTokenFlow()
        val token = runBlocking { tokenFlow.first() }
        isLoggedIn = !token.isNullOrEmpty()
        return isLoggedIn
    }
}
