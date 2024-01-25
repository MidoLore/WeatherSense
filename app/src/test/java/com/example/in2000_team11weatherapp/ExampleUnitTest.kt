package com.example.in2000_team11weatherapp

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.in2000_team11weatherapp.model.data.CurrentLocationData
import com.example.in2000_team11weatherapp.model.data.FavoriteLocation
import com.example.in2000_team11weatherapp.model.data.LatLng
import com.example.in2000_team11weatherapp.model.internet.Datasource
import com.example.in2000_team11weatherapp.model.state.TextUiState
import com.example.in2000_team11weatherapp.model.state.WeatherUiState
import com.example.in2000_team11weatherapp.ui.screens.fetchCoordinates
import com.example.in2000_team11weatherapp.ui.screens.getDayOfWeek
import com.example.in2000_team11weatherapp.viewmodel.TextToSpeechViewModel
import com.example.in2000_team11weatherapp.viewmodel.WeatherViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.threeten.bp.LocalDate
import java.net.HttpURLConnection
import java.util.Locale

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun testGetFavoriteLocations() {
        // Given
        val mockSharedPrefs = mockk<SharedPreferences> {
            for (i in 0..3) {
                every { getString("place$i", null) } returns "Place$i"
                every { getString("lat$i", null) } returns "Latitude$i"
                every { getString("long$i", null) } returns "Longitude$i"
            }
        }
        val context = mockk<Context> {
            every { getSharedPreferences(any(), any()) } returns mockSharedPrefs
        }

        val favoriteLocation = FavoriteLocation(context)

        // When
        val favoriteLocations = favoriteLocation.getFavoriteLocations()

        // Then
        val expectedLocations = List(4) { i -> Triple("Place$i", "Latitude$i", "Longitude$i") }
        assertEquals(expectedLocations, favoriteLocations)
    }

    @Test
    fun testIsFavorite() {
        val mockSharedPrefs = mockk<SharedPreferences> {
            every { getBoolean("Place", false) } returns true
        }
        val context = mockk<Context> {
            every { getSharedPreferences(any(), any()) } returns mockSharedPrefs
        }
        val favoriteLocation = FavoriteLocation(context)

        val isFavorite = favoriteLocation.isFavorite("Place")
        assertTrue(isFavorite)
    }
    @Test
    fun testOnTextFieldValueChange() {
        val context = mockk<Application>(relaxed = true)
        val appContext = mockk<Context>(relaxed = true)
        every { context.applicationContext } returns appContext

        val viewModel = TextToSpeechViewModel(context)
        viewModel.onTextFieldValueChange("test text")
        assertEquals("test text", viewModel.state.value.text)
    }
    @Test
    fun testInitialUiState() {
        val context = mockk<Application>(relaxed = true)
        val appContext = mockk<Context>(relaxed = true)
        every { context.applicationContext } returns appContext

        val viewModel = TextToSpeechViewModel(context)
        assertEquals(TextUiState(), viewModel.state.value)
    }

    /**
     * Test if the UiState is set to loading after setDataSource is called
     */
    @Test
    fun testInitialWeatherUiState() {
        val fakeViewModel = WeatherViewModel("1.0", "1.0")
        val initialUiState = fakeViewModel.weatherUiState.value
        assertNotNull(initialUiState)
    }
    @Test
    fun `setDataSource changes _weatherUiState to Loading`() = runBlocking {
        val fakeViewModel = WeatherViewModel("1.0", "1.0")
        fakeViewModel.setDataSource("2.0", "2.0")
        assertTrue(fakeViewModel.weatherUiState.value is WeatherUiState.Loading)
    }

    @Test
    fun `fetchCoordinates returns correct LatLng or null on error`() = runBlocking {
        val mockServer = MockWebServer()
        val validResponseBody = """
        {
   "html_attributions" : [],
   "result" : {
      "geometry" : {
         "location" : {
            "lat" : 59.9138688,
            "lng" : 10.7522454
         },
         "viewport" : {
            "northeast" : {
               "lat" : 59.97803498566783,
               "lng" : 10.94766406742412
            },
            "southwest" : {
               "lat" : 59.80967486051522,
               "lng" : 10.62256890842673
            }
         }
      }
   },
   "status" : "OK"
}
    """.trimIndent()

        mockServer.enqueue(
            MockResponse()
                .setBody(validResponseBody)
                .setResponseCode(HttpURLConnection.HTTP_OK)
        )

        mockServer.start()

        val correctLatLng = fetchCoordinates(mockServer.url("/").toString())

        assertEquals(LatLng(59.9138688, 10.7522454), correctLatLng)

        mockServer.shutdown()
    }

    @Test
    fun `fetchLocationData returns correct data`(): Unit = runBlocking {
        val mockServer = MockWebServer()

        val responseBody = """
        {
            "type": "Feature",
            "geometry": {
                "type": "Point",
                "coordinates": [10.75, 59.91]
            },
            "properties": {
                "meta": {
                    "updated_at": "2023-05-24T12:00:00Z"
                },
                "timeseries": []
            }
        }
    """.trimIndent()

        mockServer.enqueue(
            MockResponse()
                .setBody(responseBody)
                .setResponseCode(HttpURLConnection.HTTP_OK)
        )

        mockServer.start()
        val datasource = Datasource(mockServer.url("/").toString())

        val locationData = datasource.fetchLocationData()

        assertEquals("Feature", locationData?.type)
        assertEquals("Point", locationData?.geometry?.type)
        assertEquals(arrayListOf(10.75, 59.91), locationData?.geometry?.coordinates)
        assertEquals("2023-05-24T12:00:00Z", locationData?.properties?.meta?.updatedAt)


        // Shut down the server
        mockServer.shutdown()
    }

    @Test
    fun testGetDayOfWeek() {
        Locale.setDefault(Locale.ENGLISH)
        val dayOfWeek: String = getDayOfWeek(LocalDate.of(2023, 5, 16))
        assertEquals("Tuesday", dayOfWeek)
    }
    @Test
    fun textUiStateTest() {
        val textUiState = TextUiState(isButtonEnabled = true, text = "Hello, World!")
        assertEquals(true, textUiState.isButtonEnabled)
        assertEquals("Hello, World!", textUiState.text)
        val textUiState2 = TextUiState(isButtonEnabled = false, text = "Goodbye, World!")
        assertEquals(false, textUiState2.isButtonEnabled)
        assertEquals("Goodbye, World!", textUiState2.text)
        assertNotEquals(textUiState, textUiState2)
    }

    @Test
    fun currentLocationDataTest() {
        val latitude = 30.73
        val longitude = 13.56
        val accuracy = 100f
        val isLocationAvailable = true
        val name = "TEST CITY"

        val currentLocationData = CurrentLocationData(
            latitude = latitude,
            longitude = longitude,
            accuracy = accuracy,
            isLocationAvailable = isLocationAvailable,
            name = name
        )

        currentLocationData.latitude?.let { assertEquals(latitude, it, 0.0) }
        currentLocationData.longitude?.let { assertEquals(longitude, it, 0.0) }
        assertEquals(accuracy, currentLocationData.accuracy)
        assertEquals(isLocationAvailable, currentLocationData.isLocationAvailable)
        assertEquals(name, currentLocationData.name)
    }

}
/**
@Test
fun locationUiStateLoadingTest() {
val locationUiState = LocationUiState.Loading
assertTrue(locationUiState is LocationUiState.Loading)
}

@Test
fun locationUiStateSuccessTest() {
val locationData = CurrentLocationData()
val locationUiState = LocationUiState.Success(locationData)
assertTrue(locationUiState is LocationUiState.Success)
assertEquals(locationData, locationUiState.locationData)
}

@Test
fun locationUiStateErrorTest() {
val locationUiState = LocationUiState.Error("Error message")
assertTrue(locationUiState is LocationUiState.Error)
assertEquals("Error message", locationUiState.errorMessage)
}
@Test
fun weatherUiStateLoadingTest() {
val weatherUiState = WeatherUiState.Loading
assertTrue(weatherUiState is WeatherUiState.Loading)
}

@Test
fun weatherUiStateSuccessTest() {
val locationData = LocationData()  // Instantiate with appropriate parameters
val weatherUiState = WeatherUiState.Success(locationData)
assertTrue(weatherUiState is WeatherUiState.Success)
assertEquals(locationData, weatherUiState.locationData)
}

@Test
fun weatherUiStateErrorTest() {
val weatherUiState = WeatherUiState.Error("Error message")
assertTrue(weatherUiState is WeatherUiState.Error)
assertEquals("Error message", weatherUiState.errorMessage)
}
 */