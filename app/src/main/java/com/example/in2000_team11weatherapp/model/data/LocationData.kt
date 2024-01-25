package com.example.in2000_team11weatherapp.model.data
import com.google.gson.annotations.SerializedName

data class LocationData(
    @SerializedName("type"       ) var type       : String?     = null,
    @SerializedName("geometry"   ) var geometry   : GeometryLocation?   = GeometryLocation(),
    @SerializedName("properties" ) var properties : PropertiesLocation? = PropertiesLocation()
)

data class GeometryLocation(
    @SerializedName("type"        ) var type        : String?           = null,
    @SerializedName("coordinates" ) var coordinates : ArrayList<Double> = arrayListOf()
)

data class PropertiesLocation(
    @SerializedName("meta"       ) var meta       : Meta?                 = Meta(),
    @SerializedName("timeseries" ) var timeseries : ArrayList<TimeSeries> = arrayListOf()
)

data class Meta(
    @SerializedName("updated_at" ) var updatedAt : String? = null,
    @SerializedName("units"      ) var units     : Units?  = Units()
)

data class TimeSeries(
    @SerializedName("time" ) var time : String? = null,
    @SerializedName("data" ) var data : Data?   = Data()
)

data class Data(
    @SerializedName("instant"       ) var instant     : Instant?     = Instant(),
    @SerializedName("next_12_hours" ) var next12Hours : Next12Hours? = Next12Hours(),
    @SerializedName("next_1_hours"  ) var next1Hours  : Next1Hours?  = Next1Hours(),
    @SerializedName("next_6_hours"  ) var next6Hours  : Next6Hours?  = Next6Hours()
)

data class Instant(
    @SerializedName("details" ) var details : Details? = Details()
)

data class Next12Hours(
    @SerializedName("summary" ) var summary : Summary? = Summary()
)

data class Next1Hours(
    @SerializedName("summary" ) var summary : Summary? = Summary(),
    @SerializedName("details" ) var details : DetailsHours? = DetailsHours()
)

data class Next6Hours(
    @SerializedName("summary" ) var summary : Summary? = Summary(),
    @SerializedName("details" ) var details : DetailsHours? = DetailsHours()
)
data class DetailsHours (

    @SerializedName("precipitation_amount" ) var precipitationAmount : Double? = null

)
data class Summary(
    @SerializedName("symbol_code" ) var symbolCode : String? = null
)

data class Units(
    @SerializedName("air_pressure_at_sea_level" ) var airPressureAtSeaLevel : String? = null,
    @SerializedName("air_temperature"           ) var airTemperature        : String? = null,
    @SerializedName("cloud_area_fraction"       ) var cloudAreaFraction     : String? = null,
    @SerializedName("precipitation_amount"      ) var precipitationAmount   : String? = null,
    @SerializedName("relative_humidity"         ) var relativeHumidity      : String? = null,
    @SerializedName("wind_from_direction"       ) var windFromDirection     : String? = null,
    @SerializedName("wind_speed"                ) var windSpeed             : String? = null
)
data class Details (

    @SerializedName("air_pressure_at_sea_level" ) var airPressureAtSeaLevel : Double? = null,
    @SerializedName("air_temperature"           ) var airTemperature        : Double? = null,
    @SerializedName("cloud_area_fraction"       ) var cloudAreaFraction     : Double?    = null,
    @SerializedName("relative_humidity"         ) var relativeHumidity      : Double? = null,
    @SerializedName("wind_from_direction"       ) var windFromDirection     : Double? = null,
    @SerializedName("wind_speed"                ) var windSpeed             : Double? = null

)