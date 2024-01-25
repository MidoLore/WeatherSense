package com.example.in2000_team11weatherapp.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.in2000_team11weatherapp.R
import com.example.in2000_team11weatherapp.model.state.WeatherUiState
import com.example.in2000_team11weatherapp.ui.composables.AutoResizedText
import com.example.in2000_team11weatherapp.ui.composables.BottomNavBar
import com.example.in2000_team11weatherapp.viewmodel.LocationViewModel
import com.example.in2000_team11weatherapp.viewmodel.TextToSpeechViewModel
import com.example.in2000_team11weatherapp.viewmodel.WeatherViewModel
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.TextStyle
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(weatherViewModel: WeatherViewModel, navController: NavHostController,location: String?,locationView: LocationViewModel,textToSpeechViewModel: TextToSpeechViewModel) {
    val weatherUiState by weatherViewModel.weatherUiState.collectAsState()
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp
    val weatherCardWidth = screenWidth / 100 * 100
    val weatherCardDaysWidth = screenWidth / 100*48.625.toFloat()
    val weatherCardDaysSpacer = screenWidth/100*2.75.toFloat()
    val weatherCardHeight = screenHeight / 100 * 60
    val saveIconHeight = screenHeight / 100*7.5.toFloat()
    val buttonWidth = screenWidth / 100 * 32
    val buttonHeight = screenHeight / 100 * 15
    val calendar = Calendar.getInstance()
    val currentDate = LocalDate.now()
    val futureDates = mutableListOf<LocalDate>()

    //Gets the next days for WeatherCardDays
    futureDates.add(currentDate)
    for (i in 1..9) {
        futureDates.add(currentDate.plusDays(i.toLong()))
    }
    val selectedDate = remember { mutableStateOf(currentDate) }
    Column {
        when (weatherUiState) {
            is WeatherUiState.Loading -> {
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
                            color = Black,
                            strokeWidth = 8.dp
                        )
                    }
                }
            }
            is WeatherUiState.Success -> {
                MaterialTheme {
                    Scaffold(
                        bottomBar = {BottomNavBar(navController = navController, buttonWidth = buttonWidth, buttonHeight = buttonHeight, textToSpeechViewModel,true)},
                        content = { innerPadding ->
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(90, 44, 241, 255))
                                    .padding(innerPadding)
                                    .wrapContentWidth(Alignment.CenterHorizontally),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(modifier = Modifier
                                    .fillMaxWidth()
                                    .height(saveIconHeight)
                                    .wrapContentHeight()
                                    .align(Alignment.Start)
                                ) {
                                    var isFavorite by remember { mutableStateOf(locationView.isFavorite(location))}
                                    Image(
                                        painter = painterResource(if(isFavorite) R.drawable.baseline_star_24_yellow else R.drawable.baseline_star_border_24),
                                        contentDescription = "Star for saving",
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .align(Alignment.Center)
                                            .clickable {
                                                if (location != null && !isFavorite) {
                                                    textToSpeechViewModel.onTextFieldValueChange("Saved Location $location") // Update the text in the ViewModel
                                                    textToSpeechViewModel.textToSpeech()
                                                    isFavorite = true
                                                    (weatherUiState as WeatherUiState.Success).locationData.geometry?.coordinates
                                                        ?.get(0)
                                                        ?.let {
                                                            (weatherUiState as WeatherUiState.Success).locationData.geometry?.coordinates
                                                                ?.get(1)
                                                                ?.let { it1 ->
                                                                    locationView.saveFavoriteLocation(
                                                                        location,
                                                                        it.toString(),
                                                                        it1.toString(),
                                                                        isFavorite
                                                                    )
                                                                }
                                                        }
                                                }
                                            }
                                    )
                                }
                                LazyRow(
                                    modifier = Modifier
                                        .padding(horizontal = 0.dp),
                                    contentPadding = PaddingValues(bottom = screenHeight/100*1),
                                    horizontalArrangement = Arrangement.spacedBy(weatherCardDaysSpacer),
                                    content = {
                                        items(futureDates.size) { weatherData ->
                                            WeatherCardDays(
                                                weatherCardDaysWidth,
                                                weatherCardHeight/4,
                                                weatherUiState,
                                                futureDates[weatherData],
                                                { date -> selectedDate.value = date },
                                                textToSpeechViewModel,

                                            )
                                        }
                                    }
                                )
                                var currentHour = calendar.get(Calendar.HOUR_OF_DAY)
                                var currentDate2 = calendar.get(Calendar.DAY_OF_MONTH)
                                if (currentDate2 != selectedDate.value.dayOfMonth) {
                                    currentHour = 0
                                    currentDate2 = selectedDate.value.dayOfMonth
                                }

                                LazyRow(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 1.dp),
                                    contentPadding = PaddingValues(top=5.dp, bottom = 10.dp),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    content = {
                                        val dag = getStartHour(weatherUiState as WeatherUiState.Success,currentHour,currentDate2)
                                        val length = dag.second - dag.first
                                        items(length+1) { weatherData ->
                                            val index = weatherData + dag.first
                                            WeatherCard(
                                                weatherCardWidth,
                                                weatherCardHeight,
                                                weatherUiState,
                                                location,
                                                index,
                                                textToSpeechViewModel,

                                            )
                                        }
                                    }
                                )
                            }
                        }
                    )
                }
            }
            is WeatherUiState.Error -> {
                val errorMessage = (weatherUiState as WeatherUiState.Error).errorMessage
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = errorMessage + "Try to restart App",
                        color = Color.Red
                    )
                }
            }

        }
    }
}

/**
 * WeatherCardDays displays the day of the week and the average temperature of the day
 */
@Composable
fun WeatherCardDays(
    weatherCardWidth : Dp,
    weatherCardDaysHeight : Dp,
    Ui : WeatherUiState,
    futureDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    textToSpeechViewModel: TextToSpeechViewModel,
    ){

    (Ui as WeatherUiState.Success).locationData.geometry?.coordinates

    val formatter = DateTimeFormatter.ofPattern("yyyy-M-d'T'HH:mm:ss'Z'")
    val temperatureList = mutableListOf<Int>()
    var sum = 0
    //Gathering temperature for the day
    for (x in 0 until Ui.locationData.properties?.timeseries?.size!!) {
        val date = Ui.locationData.properties?.timeseries?.get(x)?.time
        val dateParse = LocalDate.parse(date.toString(), formatter) //formatter til LocalDate
        if (dateParse.compareTo(futureDate) == 0) {
            Ui.locationData.properties?.timeseries?.get(x)?.data?.instant?.details?.airTemperature?.let {
                temperatureList.add(
                    it.toInt()
                )
            }
        }
    }
    for(value in temperatureList) {
        sum += value
    }
    sum = sum.div(temperatureList.size) //Calculate average temperature for the day
    var sumString = sum.toString()
    if (sumString.length == 1) {
        sumString = " $sumString\u00B0 "
    } else if (sumString.length > 1) {
        sumString = "$sumString\u00B0"
    }
    val day = getDayOfWeek(futureDate)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(shape = RoundedCornerShape(16.dp))
            .width(weatherCardWidth)
            .height(weatherCardDaysHeight)
            .background(Color.White)
            .wrapContentWidth(Alignment.CenterHorizontally)
            .clickable {
                textToSpeechViewModel.onTextFieldValueChange("Day selected $day Average temperature of the day $sum Celsius. Swipe right for next day") // Update the text in the ViewModel
                textToSpeechViewModel.textToSpeech() // Call textToSpeech with the current context
                onDateSelected(futureDate)
            }
    )  {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(Color.White)
                .fillMaxWidth(0.7f)
                .weight(1f)
        ) {
            AutoResizedText(
                text = day,
                isWeatherCardDays = true,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(Color.White)
                .fillMaxWidth(0.4f)
                .weight(1f)
        ) {
            AutoResizedText(
                text = sumString,
                isWeatherCardDays = true,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

/**
 *WeatherCard Displays the name, hour, weather depiction and temperature
 */
@Composable
fun WeatherCard(
    weatherCardWidth: Dp,
    weatherCardHeight: Dp,
    ui: WeatherUiState,
    location: String?,
    index: Int,
    textToSpeechViewModel: TextToSpeechViewModel,
    ) {
    val data = (ui as WeatherUiState.Success).locationData.geometry?.coordinates
    val tid = ui.locationData.properties?.timeseries?.get(index)?.time.toString()
    val temp = ui.locationData.properties?.timeseries?.get(index)?.data?.instant?.details?.airTemperature.toString()
    val symbolCode: String? = ui.locationData.properties?.timeseries?.get(index)?.data?.next1Hours?.summary?.symbolCode
    val resourceId = symbolCode ?: "black_question_mark"
    val locationString = location ?: "Unknown location"
    val parts = locationString.split(",", limit = 2)
    val firstPart = parts[0].trim()
    val timeParts = tid.split("T", ":")
    val hour = timeParts[1]
    val minute = timeParts[2]
    val formattedTime = "$hour:$minute"

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(shape = RoundedCornerShape(16.dp))
            .width(weatherCardWidth)
            .height(weatherCardHeight)
            .background(Color.White)
            .wrapContentWidth(Alignment.CenterHorizontally)
            .clickable {
                val textToSpeech =
                    "Location is $location Time is $formattedTime Temperature is $temp Celsius.  Description is$resourceId.  Swipe right for next hour"
                textToSpeechViewModel.onTextFieldValueChange(textToSpeech) // Update the text in the ViewModel
                textToSpeechViewModel.textToSpeech() // Call textToSpeech with the current context
            }
    )  {
        if (data != null) {
            val firstParts = if (firstPart.length > 10) "${firstPart.take(10)}..." else firstPart   // Ensure that the length of the name is not over 10
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .weight(1f)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                AutoResizedText(
                    text = firstParts,
                    fontWeight = FontWeight.Bold,
                    color = Black
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth(0.3f)
                .weight(0.8f)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            AutoResizedText(
                text = formattedTime,
                fontWeight = FontWeight.SemiBold,
                color = Black
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(3f)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = getResourceId(resourceId)),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .weight(1.3f)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            AutoResizedText(
                text = temp + "\u00B0",
                fontWeight = FontWeight.Bold,
                color = Black
            )
        }
        Box(
            modifier = Modifier
                .weight(0.15f)
                .background(Color.White),
        )
    }
}

/**
 * This function gets the right Drawable resource to the Composable functions
 */
@SuppressLint("DiscouragedApi")
@Composable
private fun getResourceId(filename: String): Int {
    val context = LocalContext.current
    val packageName = context.packageName
    return context.resources.getIdentifier(filename, "drawable", packageName)
}

/**
 * getStartHour gets you the start and end of the day, so that we can compute the start index of the hour and
 * how many hours there are left
 */
fun getStartHour(Ui: WeatherUiState.Success, currentHour: Int, date: Int): Pair<Int, Int> {
    val timeSeries = Ui.locationData.properties?.timeseries
    var start = 0
    var end = 0
    var startBool = false   // Variable to track whether the start index has been found.
    timeSeries?.let {
        for (index in timeSeries.indices) {
            val entryTime = timeSeries[index].time.toString()
            // Extract the hour and date from the entry time string.
            val entryHour = entryTime.substring(11, 13)
            val entryDate = entryTime.substring(8, 10)
            // Check if the entry hour and date match the current hour and date.
            if (entryHour.toInt() == currentHour && entryDate.toInt() == date) {
                start = index
                startBool = true
            }
            // Check if the entry hour is "23" and the start index has been found.
            if (entryHour == "23" && startBool) {
                return Pair(start, index)
            }
            // Check if startBool is true and the entry date is different from the current date.
            if (startBool && entryDate.toInt() != date) {
                return Pair(start, index - 1)
            }
            end = index
        }
    }
    // Recursively call getStartHour with the previous date to find the start index outside the loop.
    val outsideLoop = getStartHour(Ui, currentHour, date - 1)
    return Pair(outsideLoop.second + 1, end)
}
fun getDayOfWeek(date: LocalDate): String {
    val dayOfWeek = date.dayOfWeek // Get the day of the week as a DayOfWeek object
    return dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()) // Convert the DayOfWeek object to its corresponding string representation

}
