package com.example.in2000_team11weatherapp.model.data

/**
 * CurrentLocationData is a data class that holds the current location the user has registered
 */
data class CurrentLocationData(
    val latitude: Double? = 59.91,
    val longitude: Double? = 10.75,
    val accuracy: Float? = null,
    val isLocationAvailable: Boolean = false,
    val name: String? = "Oslo"
)
