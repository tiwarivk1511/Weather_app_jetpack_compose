package com.android.weather.data

data class AirQualityResponse(
    val hourly: HourlyData
)

data class HourlyData(
    val pm10: List<Double>,
    val pm2_5: List<Double>,
    val carbon_monoxide: List<Double>,
    val nitrogen_dioxide: List<Double>,
    val sulphur_dioxide: List<Double>,
    val ozone: List<Double>
)

