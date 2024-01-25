package com.example.in2000_team11weatherapp.model.internet

import android.util.Log
import com.example.in2000_team11weatherapp.model.data.LocationData
import com.google.gson.Gson
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.gson.*

/**
 * Class to use Ktor too load weather information
 */
class Datasource(private val path: String) {
    suspend fun fetchLocationData(): LocationData? {
        val client = HttpClient {
            install(ContentNegotiation) {
                gson()
            }
        }
        try {
            val response: HttpResponse = client.get(path){
                header("X-Gravitee-API-Key","15a64444-623f-4016-8753-20d67e8bc077")
            }
            val responseText = response.bodyAsText()
            val gson = Gson()
            return gson.fromJson(responseText, LocationData::class.java)
        } catch (e: Exception) {
            Log.e("WeatherViewModel", "Error fetching data: ${e.message}")
            throw e
        } finally {
            client.close()
        }
    }
}