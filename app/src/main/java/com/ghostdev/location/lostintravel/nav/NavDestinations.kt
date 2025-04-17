package com.ghostdev.location.lostintravel.nav

sealed class NavDestinations(val route: String) {
    object Loading : NavDestinations("loading")
    object Onboarding : NavDestinations("onboarding")
    object SignIn : NavDestinations("signin")
    object SignUp : NavDestinations("signup")
    object Home : NavDestinations("home")
}