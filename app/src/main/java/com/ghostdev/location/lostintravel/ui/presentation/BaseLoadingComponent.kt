package com.ghostdev.location.lostintravel.ui.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.ghostdev.location.lostintravel.R
import com.ghostdev.location.lostintravel.ui.presentation.onboarding.OnboardingLogic
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun BaseLoadingComponent(
    modifier: Modifier,
    navigateToOnboarding: () -> Unit = {},
    navigateToHome: () -> Unit = {},
    viewmodel: OnboardingLogic = koinViewModel()
) {
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.loading_anim)
    )
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        delay(1500)
        if (viewmodel.isUserLoggedIn(context)) {
            navigateToHome()
        } else {
            navigateToOnboarding()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            modifier = Modifier.size(120.dp),
            composition = composition,
            iterations = Int.MAX_VALUE,
        )
    }
}

@Composable
fun LoadingComponent(
    modifier: Modifier = Modifier
) {
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.loading_anim)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White.copy(alpha = 0.3f)),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            modifier = Modifier.size(120.dp),
            composition = composition,
            iterations = Int.MAX_VALUE,
        )
    }
}