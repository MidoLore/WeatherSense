package com.example.in2000_team11weatherapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.in2000_team11weatherapp.MainActivity.Destination
import com.example.in2000_team11weatherapp.model.data.CurrentLocationData
import com.example.in2000_team11weatherapp.model.data.LatLng
import com.example.in2000_team11weatherapp.ui.screens.LocationScreen
import com.example.in2000_team11weatherapp.ui.screens.MainScreen
import com.example.in2000_team11weatherapp.ui.screens.SearchScreen
import com.example.in2000_team11weatherapp.ui.screens.SettingScreen
import com.example.in2000_team11weatherapp.ui.theme.IN2000Team11WeatherappTheme
import com.example.in2000_team11weatherapp.viewmodel.LocationViewModel
import com.example.in2000_team11weatherapp.viewmodel.TextToSpeechViewModel
import com.example.in2000_team11weatherapp.viewmodel.WeatherViewModel
import com.jakewharton.threetenabp.AndroidThreeTen


private const val PERMISSION_REQUEST_CODE = 1

class MainActivity : ComponentActivity() {
    sealed class Destination(val route: String) {
        object MainScreen: Destination("MainScreen")
        object SearchScreen: Destination("SearchScreen")
        object SettingScreen: Destination("SettingScreen")
        object LocationScreen: Destination("LocationScreen")
    }

    /**
     * This part of the code asks the user for permission to track
     * It has double checks so that the app starts either way
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidThreeTen.init(this)
        var whichMain = false
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), PERMISSION_REQUEST_CODE)
            whichMain = true
        } else {
            startMain()
        }
        if (whichMain) { startMain() }
    }

    private fun startMain() {
        Log.d("startMain()", "called")
        setContent {
            IN2000Team11WeatherappTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavigationAppHost(navController = navController)
                }
            }
        }
    }
}

@Composable
fun NavigationAppHost(navController: NavHostController) {
    val locationUi = CurrentLocationData()
    var location = "Oslo"
    val weatherViewModel = WeatherViewModel(locationUi.latitude.toString(), locationUi.longitude.toString())
    val locationViewModel: LocationViewModel = viewModel()
    val textToSpeechViewModel: TextToSpeechViewModel = viewModel()
    NavHost(navController = navController, startDestination = Destination.LocationScreen.route) {
        composable(Destination.LocationScreen.route) { LocationScreen(navController, weatherViewModel,locationViewModel,textToSpeechViewModel) }
        composable(Destination.MainScreen.route) { MainScreen(weatherViewModel, navController,location,locationViewModel,textToSpeechViewModel) }
        composable(Destination.SearchScreen.route) { SearchScreen(navController, onSearch = { query: Pair<String, LatLng> ->
            location = query.first
            weatherViewModel.setDataSource(query.second.latitude.toString(), query.second.longitude.toString())
            navController.navigate(Destination.MainScreen.route) }, locationViewModel,textToSpeechViewModel) }
        composable(Destination.SettingScreen.route) { SettingScreen(navController,textToSpeechViewModel) }
    }
}