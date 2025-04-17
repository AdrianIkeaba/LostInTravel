package com.ghostdev.location.lostintravel

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.ghostdev.location.lostintravel.di.initKoin
import com.ghostdev.location.lostintravel.nav.NavGraph
import com.ghostdev.location.lostintravel.ui.theme.LostInTravelTheme
import org.koin.core.context.stopKoin
import android.Manifest
import android.util.Log
import com.ghostdev.location.lostintravel.ui.presentation.signup.SignUpLogic
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (fineLocationGranted || coarseLocationGranted) {
            val signUpLogic: SignUpLogic by inject()
            signUpLogic.fetchCurrentLocation()
        } else {
            Toast.makeText(
                this,
                "Location permission is required for this app to function properly",
                Toast.LENGTH_LONG
            ).show()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initKoin(
            this@MainActivity
        )
        enableEdgeToEdge()
        if (!hasLocationPermission()) {
            requestLocationPermissions()
        }
        setContent {
            LostInTravelTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavGraph(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        stopKoin()
    }

    private fun hasLocationPermission(): Boolean {
        val fineLocation = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        val coarseLocation = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        return fineLocation == android.content.pm.PackageManager.PERMISSION_GRANTED ||
                coarseLocation == android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    fun requestLocationPermissions() {
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
}