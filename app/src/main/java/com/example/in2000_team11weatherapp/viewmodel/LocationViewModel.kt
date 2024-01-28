package com.example.in2000_team11weatherapp.viewmodel

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.in2000_team11weatherapp.model.data.CurrentLocationData
import com.example.in2000_team11weatherapp.model.data.FavoriteLocation
import com.example.in2000_team11weatherapp.model.state.LocationUiState
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.gson.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class LocationViewModel(application: Application) : AndroidViewModel(application) {
    private val favoriteLocationsHelper = FavoriteLocation(application)
    private val locationManager = application.getSystemService(LocationManager::class.java)
    private val _locationUiNewState = MutableStateFlow<LocationUiState>(LocationUiState.Loading)
    val locationUiNewState = _locationUiNewState.asStateFlow()
    init {
        getCurrentLocation()
    }

    /**
     * This function should get the locations name with the help of lat and long
     */
    suspend fun getLocationName(latitude: Double,longitude: Double): String? {
        /*
        val url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=$latitude,$longitude&result_type=locality&key=$apiKey"
        val client = HttpClient(Android) {
            install(ContentNegotiation) {
                gson()
            }
        }
        try {
            val response: HttpResponse = client.get(url)
            val responseText = response.bodyAsText()

            val json = Json.parseToJsonElement(responseText).jsonObject
            val results = json["results"]?.jsonArray

            return if (results != null && results.isNotEmpty()) {
                val firstResult = results.first().jsonObject
                firstResult["formatted_address"]?.jsonPrimitive?.content
            } else {
                null
            }
        }
        catch (e: Exception) {
            Log.e("LocationViewModel", "Error fetching data: ${e.message}")
            throw e
        } finally {
            client.close()
        }
         */
        return "Current Location"
    }

    private fun getCurrentLocation() {
        _locationUiNewState.value = LocationUiState.Loading
        if (ActivityCompat.checkSelfPermission(getApplication(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getApplication(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            _locationUiNewState.value = LocationUiState.Success(
                CurrentLocationData(
                    59.91,
                    10.75,
                    null,
                    true,
                    "Oslo"
                )
            )
            return
        }
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            10000L,
            400f,
            object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    // when location switch
                    viewModelScope.launch {
                        val place = getLocationName(location.latitude, location.longitude)
                        _locationUiNewState.value = LocationUiState.Success(
                            CurrentLocationData(
                                location.latitude,
                                location.longitude,
                                location.accuracy,
                                true,
                                place
                            )
                        )
                    }
                }
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
                @Deprecated("Deprecated in Java")
                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            }
        )
    }

    fun removeLocationUpdates() {
        //locationManager.removeUpdates(locationListener)
    }

    override fun onCleared() {
        removeLocationUpdates()
        super.onCleared()
    }

    fun saveFavoriteLocation(string1: String, string2: String, string3: String, isFavorite: Boolean) {
        Log.d("LocationViewModel", "$string1 $string2 $string3")
        favoriteLocationsHelper.saveFavoriteLocation(string1, string2, string3, isFavorite)
    }

    fun getFavoriteLocations(): List<Triple<String?, String?, String?>> {
        return favoriteLocationsHelper.getFavoriteLocations()
    }

    fun isFavorite(location: String?): Boolean{
        return favoriteLocationsHelper.isFavorite(location)
    }
}
