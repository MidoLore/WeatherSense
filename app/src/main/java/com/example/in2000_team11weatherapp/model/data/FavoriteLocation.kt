package com.example.in2000_team11weatherapp.model.data

import android.content.Context
class FavoriteLocation(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("favorite_locations", Context.MODE_PRIVATE)

    fun saveFavoriteLocation(string1: String?, string2: String?, string3: String?, isFavorite: Boolean) {
        val locationIndex = sharedPreferences.getInt("locationIndex", 0)
        sharedPreferences.edit()
            .putString("place$locationIndex", string1)
            .putString("lat$locationIndex", string2)
            .putString("long$locationIndex", string3)
            .putBoolean(string1, isFavorite)
            .putInt("locationIndex", (locationIndex + 1) % 4) // Rotate through indices 0-3
            .apply()
    }

    //for save
    fun isFavorite(location: String?): Boolean{
        return sharedPreferences.getBoolean(location, false)
    }

    fun getFavoriteLocations(): List<Triple<String?, String?, String?>> {
        val locations = mutableListOf<Triple<String?, String?, String?>>()
        for (i in 0 until 4) { // We have 4 locations
            val string1 = sharedPreferences.getString("place$i", null)
            val string2 = sharedPreferences.getString("lat$i", null)
            val string3 = sharedPreferences.getString("long$i", null)
            if (string1 != null && string2 != null && string3 != null) {
                locations.add(Triple(string1, string2, string3))
            }
        }
        return locations
    }
}
