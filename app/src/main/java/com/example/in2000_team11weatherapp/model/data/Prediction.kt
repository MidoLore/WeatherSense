package com.example.in2000_team11weatherapp.model.data

data class AutocompleteResponse(
    val predictions: List<Prediction>,
    val status: String
)

data class Prediction(
    val description: String,
    val id: String,
    val place_id: String,
    val reference: String
)
data class LatLng(val latitude: Double, val longitude: Double)
data class PlaceDetailsResponse(
    val result: PlaceResult,
    val status: String
)

data class PlaceResult(
    val geometry: Geometry
)

data class Geometry(
    val location: Location
)

data class Location(
    val lat: Double,
    val lng: Double
)
