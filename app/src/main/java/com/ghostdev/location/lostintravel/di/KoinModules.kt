package com.ghostdev.location.lostintravel.di

import android.content.Context
import com.ghostdev.location.lostintravel.data.remote.PlacesRemoteDataSource
import com.ghostdev.location.lostintravel.data.remote.UserRemoteDataSource
import com.ghostdev.location.lostintravel.data.repositories.PlacesRepository
import com.ghostdev.location.lostintravel.data.repositories.PlacesRepositoryImpl
import com.ghostdev.location.lostintravel.data.repositories.UserRepository
import com.ghostdev.location.lostintravel.data.repositories.UserRepositoryImpl
import com.ghostdev.location.lostintravel.data.service.LocationService
import com.ghostdev.location.lostintravel.data.service.LocationServiceImpl
import com.ghostdev.location.lostintravel.data.usecase.CreateUserUseCase
import com.ghostdev.location.lostintravel.data.usecase.GetUserFullNameUseCase
import com.ghostdev.location.lostintravel.data.usecase.LogOutUseCase
import com.ghostdev.location.lostintravel.data.usecase.LoginUseCase
import com.ghostdev.location.lostintravel.data.usecase.RecommendedPlacesUseCase
import com.ghostdev.location.lostintravel.ui.presentation.home.HomeLogic
import com.ghostdev.location.lostintravel.ui.presentation.onboarding.OnboardingLogic
import com.ghostdev.location.lostintravel.ui.presentation.signin.SignInLogic
import com.ghostdev.location.lostintravel.ui.presentation.signup.SignUpLogic
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

private val provideViewModel = module {
    factoryOf(::OnboardingLogic)
    factoryOf(::HomeLogic)
    factoryOf(::SignInLogic)
    factoryOf(::SignUpLogic)
    factoryOf(::OnboardingLogic)
}

private val provideDataSources = module {
    factory { UserRemoteDataSource(androidContext()) }
    factory { PlacesRemoteDataSource(androidContext()) }
}

private val provideRepository = module {
    single<UserRepository> { UserRepositoryImpl(androidContext()) }
    single<PlacesRepository> { PlacesRepositoryImpl(androidContext()) }
}

private val provideUseCases = module {
    factory { CreateUserUseCase() }
    factory { LoginUseCase() }
    factory { GetUserFullNameUseCase() }
    factory { RecommendedPlacesUseCase() }
    factory { LogOutUseCase() }
}

private val provideService = module {
    single<LocationService> { LocationServiceImpl(androidContext()) }
}



fun initKoin(
    context: Context
) {
    startKoin {
        androidContext(context)
        modules(
            provideViewModel,
            provideDataSources,
            provideRepository,
            provideUseCases,
            provideService
        )
    }
}