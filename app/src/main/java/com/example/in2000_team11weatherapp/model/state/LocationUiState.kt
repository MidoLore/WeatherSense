package com.example.in2000_team11weatherapp.model.state

import com.example.in2000_team11weatherapp.model.data.CurrentLocationData

sealed class LocationUiState {
    object Loading : LocationUiState()
    data class Success(val locationData: CurrentLocationData) : LocationUiState()
    data class Error(val errorMessage: String) : LocationUiState()
}
