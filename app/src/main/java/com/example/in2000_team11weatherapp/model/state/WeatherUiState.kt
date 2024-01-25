package com.example.in2000_team11weatherapp.model.state
import com.example.in2000_team11weatherapp.model.data.LocationData

/**
 * Class to keep the UI state
 */
sealed class WeatherUiState {
    object Loading : WeatherUiState()
    data class Success(val locationData: LocationData) : WeatherUiState()
    data class Error(val errorMessage: String) : WeatherUiState()
}

