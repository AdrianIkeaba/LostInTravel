package com.ghostdev.location.lostintravel.data.models

data class LocationData(
    val latitude: String,
    val longitude: String,
    val city: String = "",
    val state: String = "",
    val country: String = "",
    val displayName: String = ""
)