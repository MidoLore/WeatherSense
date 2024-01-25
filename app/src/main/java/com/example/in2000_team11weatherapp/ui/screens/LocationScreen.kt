package com.example.in2000_team11weatherapp.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.in2000_team11weatherapp.model.state.LocationUiState
import com.example.in2000_team11weatherapp.viewmodel.LocationViewModel
import com.example.in2000_team11weatherapp.viewmodel.TextToSpeechViewModel
import com.example.in2000_team11weatherapp.viewmodel.WeatherViewModel


/**
 * LocationScreen is a composable function that observes the location from the user
 * with help from LocationViewModel. It waits until it got the coordinates and then "starts" the app.
 */
@Composable
fun LocationScreen(navController: NavHostController, weatherViewModel: WeatherViewModel, locationViewModel: LocationViewModel,textToSpeechViewModel: TextToSpeechViewModel) {
    val locationState by locationViewModel.locationUiNewState.collectAsState()
    var keepGoing by remember { mutableStateOf(false) }

    Column {
        when(locationState) {
            is LocationUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Loading",
                            fontSize = 50.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(
                            modifier = Modifier.height(30.dp)
                        )
                        CircularProgressIndicator(
                            color = Color.Black,
                            strokeWidth = 8.dp
                        )
                    }
                }
            }
            is LocationUiState.Success -> {
                val data = (locationState as LocationUiState.Success).locationData
                weatherViewModel.setDataSource(data.latitude.toString(), data.longitude.toString())
                keepGoing = true
            }
            else -> {}
        }
    }
    if (keepGoing) {
        val data = (locationState as LocationUiState.Success).locationData
        MainScreen(weatherViewModel, navController, data.name,locationViewModel,textToSpeechViewModel)
    }
    DisposableEffect(Unit) {
        onDispose {
            locationViewModel.removeLocationUpdates()
        }
    }
}