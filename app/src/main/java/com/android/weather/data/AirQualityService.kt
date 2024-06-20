package com.android.weather.data

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface AirQualityService {
    @GET("v1/air-quality")
    fun getAirQuality(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("hourly") hourly: String,
        @Query("timezone") timezone: String
    ): Call<AirQualityResponse>
}
