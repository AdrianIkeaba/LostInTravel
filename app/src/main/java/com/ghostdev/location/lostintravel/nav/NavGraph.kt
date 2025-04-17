package com.ghostdev.location.lostintravel.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ghostdev.location.lostintravel.ui.presentation.BaseLoadingComponent
import com.ghostdev.location.lostintravel.ui.presentation.home.HomeComponent
import com.ghostdev.location.lostintravel.ui.presentation.onboarding.LoginComponent
import com.ghostdev.location.lostintravel.ui.presentation.signin.SignInComponent
import com.ghostdev.location.lostintravel.ui.presentation.signup.SignUpComponent

@Composable
fun NavGraph(
    modifier: Modifier,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavDestinations.Loading.route
    ) {
        composable(
            route = NavDestinations.Loading.route
        ) {
            BaseLoadingComponent(
                modifier = modifier,
                navigateToHome = {
                    navController.navigate(NavDestinations.Home.route) {
                        popUpTo(NavDestinations.Home.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                navigateToOnboarding = {
                    navController.navigate(NavDestinations.Onboarding.route) {
                        popUpTo(NavDestinations.Onboarding.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = NavDestinations.Onboarding.route
        ) {
            LoginComponent(
                modifier = modifier,
                navigateToSignUp = {
                    navController.navigate(NavDestinations.SignUp.route)
                },
                navigateToSignIn = {
                    navController.navigate(NavDestinations.SignIn.route)
                }
            )
        }

        composable(
            route = NavDestinations.SignUp.route
        ) {
            SignUpComponent(
                modifier = modifier,
                navigateToSignIn = {
                    navController.navigate(NavDestinations.SignIn.route)
                }
            )
        }

        composable(
            route = NavDestinations.SignIn.route
        ) {
            SignInComponent(
                modifier = modifier,
                navigateToSignUp = {
                    navController.navigate(NavDestinations.SignUp.route)
                },
                navigateToHome = {
                    navController.navigate(NavDestinations.Home.route)
                }
            )
        }

        composable(
            route = NavDestinations.Home.route
        ) {
            HomeComponent(
                modifier = modifier,
                navigateToOnboarding = {
                    navController.navigate(NavDestinations.Onboarding.route) {
                        popUpTo(NavDestinations.Onboarding.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}