package com.example.in2000_team11weatherapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.in2000_team11weatherapp.model.internet.Datasource
import com.example.in2000_team11weatherapp.model.state.WeatherUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class WeatherViewModel(latitude: String, longitude: String) : ViewModel() {
    private val _weatherUiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val weatherUiState = _weatherUiState.asStateFlow()
    private var dataSource = Datasource("https://gw-uio.intark.uh-it.no/in2000/weatherapi/locationforecast/2.0/compact.json?altitude=1&lat=$latitude&lon=$longitude")

    init {
        getLocationData()
    }

    /**
     * setDataSource() is a method that's being called from SearchScreen and LocationViewModel
     *  it updates coordinates and starts getLocationData()
     */
    fun setDataSource(newLatitude: String, newLongitude: String) {
        dataSource = Datasource("https://gw-uio.intark.uh-it.no/in2000/weatherapi/locationforecast/2.0/compact.json?altitude=1&lat=$newLatitude&lon=$newLongitude")
        getLocationData()
    }

    /**
     * getLocationData() is a method that launches a coroutine scope that starts fetchLocationData()
     * from DataSource
     */
    private fun getLocationData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val locationData = dataSource.fetchLocationData()
                _weatherUiState.value = locationData?.let { WeatherUiState.Success(locationData = it) }!!
            } catch (e: Exception) {
                _weatherUiState.value = WeatherUiState.Error("Failed to fetch location data: ${e.message}")
            }
        }
    }
}
