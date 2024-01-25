package com.example.in2000_team11weatherapp.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.in2000_team11weatherapp.model.data.*
import com.example.in2000_team11weatherapp.ui.composables.AutoResizedText
import com.example.in2000_team11weatherapp.ui.composables.BottomNavBar
import com.example.in2000_team11weatherapp.viewmodel.LocationViewModel
import com.example.in2000_team11weatherapp.viewmodel.TextToSpeechViewModel
import com.google.gson.Gson
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.gson.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


// AIzaSyAA1JfXgvYgKfmUyl8Fe3JZJdIk867rPbw API KEY
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavHostController, onSearch: (Pair<String, LatLng>) -> Unit, locationViewModel: LocationViewModel,textToSpeechViewModel: TextToSpeechViewModel) {
    val modifier = Modifier
        .fillMaxWidth()
        .background(Color(red = 89, green = 40, blue = 237))
        .wrapContentSize(Alignment.Center)
    var query by remember { mutableStateOf(TextFieldValue("")) }
    var suggestions by remember { mutableStateOf(emptyList<Pair<String,String>>()) }
    val focusRequester = remember{FocusRequester() }
    val favoriteLocations = remember { mutableStateOf(List(4) { Triple<String?, String?, String?>(null, null, null) }) }
    val focusManager = LocalFocusManager.current
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val saveCardsWidth = screenWidth / 100 * 96
    val titleHeight = screenHeight / 100 * 15
    val titleToSearchBarHeight = screenHeight / 100 * 5
    val searchBarHeight = screenHeight / 100 * 15
    val buttonWidth = screenWidth / 100 * 32
    val buttonHeight = screenHeight / 100 * 15


    //https://maps.googleapis.com/maps/api/place/autocomplete/json?input=os&key=AIzaSyAA1JfXgvYgKfmUyl8Fe3JZJdIk867rPbw"
    /**
     * This Launched Effect gets suggestions when the user start typing
     */
    LaunchedEffect(query.text) {
        favoriteLocations.value = locationViewModel.getFavoriteLocations()
        delay(100)
        val apiKey = "AIzaSyAA1JfXgvYgKfmUyl8Fe3JZJdIk867rPbw"
        val input = query.text
        val url = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=$input&key=$apiKey"
        val client = HttpClient(Android) {
            install(ContentNegotiation) {
                gson{
                    setLenient()
                }
                //gson()
            }
        }
        try {
            val response: HttpResponse = client.get(url)
            val responseText = response.bodyAsText()
            val gson = Gson()
            val autocomplete = gson.fromJson(responseText, AutocompleteResponse::class.java)
            suggestions = autocomplete.predictions.map { it.description to it.place_id }
        } catch (e: Exception) {
            Log.e("SearchScreen", "Error fetching data: ${e.message}")
            throw e
        } finally {
            client.close()
        }
    }
    Scaffold (
        bottomBar =  {BottomNavBar(navController = navController, buttonWidth = buttonWidth, buttonHeight = buttonHeight, textToSpeechViewModel,true)},
        content = { innerPadding ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = modifier
                        .height(titleHeight)
                        .padding(horizontal = titleToSearchBarHeight)
                ) {
                    AutoResizedText(
                        text = "Location",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                Spacer(
                    modifier = modifier.height(screenHeight/100*1)
                )
                val fontSize = searchBarHeight.value.toInt() / 2
                TextField(
                        value = query,
                        onValueChange = { query = it },
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor =  Color.White
                        ),
                        placeholder = {
                            Text(
                                text = "Search",
                                style = TextStyle(fontSize = fontSize.sp/1.1, lineHeight = searchBarHeight.value.sp),
                                color = Color.Black,
                                fontWeight = FontWeight.SemiBold,
                            )
                        },
                        textStyle = TextStyle(fontSize = fontSize.sp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(searchBarHeight)
                            .padding(horizontal = 16.dp)
                            .focusRequester(focusRequester)
                            .onFocusChanged { focusState ->
                                if (!focusState.isFocused) {
                                    focusManager.clearFocus()
                                }
                            },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                    )

                    if (query.text.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .fillMaxWidth()
                                .background(Color.White)

                        ) {
                            items(suggestions.size) { index ->
                                val suggest = suggestions[index].first
                                val placeId = suggestions[index].second
                                LocationSuggestion(
                                    location = Prediction(suggest, "",placeId,""),
                                    onSuggestionClick = { description, coordinates ->
                                        query = TextFieldValue(text = description)
                                        onSearch(Pair(description, coordinates))
                                    }
                                )
                            }
                        }
                    }
                SavedLocations(
                    favoriteLocations = favoriteLocations,
                    saveCardsWidth = saveCardsWidth,
                   onSearchLocation = onSearch
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedLocations(
    favoriteLocations: MutableState<List<Triple<String?, String?, String?>>>,
    saveCardsWidth: Dp,
    onSearchLocation: (Pair<String, LatLng>) -> Unit
) {
    Column(
        modifier = Modifier
            .width(saveCardsWidth)
            .fillMaxHeight()
            .background(Color(red = 89, green = 40, blue = 237)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val size = favoriteLocations.value.size
        Spacer(
            modifier = Modifier.weight(0.1f)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color(red = 89, green = 40, blue = 237)),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Card(
                modifier = Modifier
                    .fillMaxHeight()
                    .background(Color(red = 89, green = 40, blue = 237))
                    .weight(1f)
                    .clickable(enabled = size >= 1) {
                        val description = favoriteLocations.value[0].first.toString()
                        val lat = favoriteLocations.value[0].second?.toDouble()
                        val long = favoriteLocations.value[0].third?.toDouble()
                        if (lat != null && long != null) {
                            onSearchLocation(Pair(description, LatLng(long, lat)))
                        }
                    }
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .padding(horizontal = 10.dp)
                        .weight(1f)
                ) {
                    if(size >=1) {
                        AutoResizedText(
                            text = favoriteLocations.value[0].first.toString().split(",")[0],
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
            Spacer(
                modifier = Modifier.weight(0.1f)
            )
            Card(
                modifier = Modifier
                    .fillMaxHeight()
                    .background(Color(red = 89, green = 40, blue = 237))
                    .weight(1f)
                    .clickable(enabled = size >= 2) {
                        val lat2 = favoriteLocations.value[1].second?.toDouble()
                        val long2 = favoriteLocations.value[1].third?.toDouble()
                        val description2: String = favoriteLocations.value[1].first.toString()
                        if (lat2 != null && long2 != null) {
                            onSearchLocation(Pair(description2, LatLng(long2, lat2)))
                        }
                    }
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .padding(horizontal = 10.dp)
                        .weight(1f)
                ) {
                    if(size >=2) {
                        AutoResizedText(
                            text = favoriteLocations.value[1].first.toString().split(",")[0],
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
        Spacer(
            modifier = Modifier.weight(0.1f)
        )
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color(red = 89, green = 40, blue = 237)),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Card(
                modifier = Modifier
                    .fillMaxHeight()
                    .background(Color(red = 89, green = 40, blue = 237))
                    .weight(1f)
                    .clickable(enabled = size >= 3) {

                        val description = favoriteLocations.value[2].first.toString()
                        val lat = favoriteLocations.value[2].second?.toDouble()
                        val long = favoriteLocations.value[2].third?.toDouble()

                        if (lat != null && long != null) {
                            onSearchLocation(Pair(description, LatLng(long, lat)))
                        }
                    }
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .padding(horizontal = 10.dp)
                ) {
                    if (size >= 3) {
                        AutoResizedText(
                            text = favoriteLocations.value[2].first.toString().split(",")[0],
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
            Spacer(
                modifier = Modifier.weight(0.1f)
            )
            Card(
                modifier = Modifier
                    .background(Color(red = 89, green = 40, blue = 237))
                    .fillMaxHeight()
                    .weight(1f)
                    .clickable(enabled = size >= 4) {

                        val description = favoriteLocations.value[3].first.toString()
                        val lat = favoriteLocations.value[3].second?.toDouble()
                        val long = favoriteLocations.value[3].third?.toDouble()

                        print("lat: $lat \n og long: $long")
                        print("description: $description")
                        if (lat != null && long != null) {
                            onSearchLocation(Pair(description, LatLng(long, lat)))
                        }
                    }
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .padding(horizontal = 10.dp)
                ) {
                    if(size >=4) {
                        AutoResizedText(
                            text = favoriteLocations.value[3].first.toString().split(",")[0],
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
        Spacer(
            modifier = Modifier.weight(0.1f)
        )
    }
}


/**
 * LocationSuggestion() takes a Prediction-object and a on onSuggestionClick()
 * Then we call fetchCoordinates to get them and send them to MainActivity
 */
@Composable
fun LocationSuggestion(location: Prediction, onSuggestionClick: (String, LatLng) -> Unit) {
    val apiKey = "AIzaSyAA1JfXgvYgKfmUyl8Fe3JZJdIk867rPbw"
    Text(
        text = location.description,
        color = Color.Black,
        fontSize = 34.sp,
        lineHeight = 36.sp,
        maxLines = 4,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier
            .padding(vertical = 5.dp)
            .padding(horizontal = 16.dp)
            .clickable {
                CoroutineScope(Dispatchers.Main).launch {
                    val url =
                        "https://maps.googleapis.com/maps/api/place/details/json?place_id=${location.place_id}&fields=geometry&key=$apiKey"
                    val coordinates = fetchCoordinates(url)
                    coordinates?.let {
                        onSuggestionClick(location.description, it)
                    }
                }
            }
    )
}

/**
 * fetchCoordinates is a suspended function that fetches coordinates using google places api
 */
suspend fun fetchCoordinates(url: String): LatLng? {
    val client = HttpClient {
        install(ContentNegotiation) { gson() }
    }
    return try {
        val response: HttpResponse = client.get(url)
        val responseText = response.bodyAsText()
        val gson = Gson()
        val placeDetailsResponse = gson.fromJson(responseText, PlaceDetailsResponse::class.java)
        val location = placeDetailsResponse.result.geometry.location
        LatLng(location.lat, location.lng)
    } catch (e: Exception) {
        Log.e("SearchScreen", "Error fetching coordinates: ${e.message}")
        null
    } finally {
        client.close()
    }
}