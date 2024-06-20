package com.android.weather.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.android.weather.R
import com.android.weather.components.AqiCard
import com.android.weather.components.SunriseSunsetCard
import com.android.weather.components.WeatherItem
import com.android.weather.data.AirQualityResponse
import com.android.weather.data.RetrofitInstance
import com.android.whether.ApiInterface
import com.android.whether.WeatherApp
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@Composable
fun MarqueeText(text: String, modifier: Modifier = Modifier) {
    var width by remember { mutableIntStateOf(0) }
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            while (true) {
                scrollState.animateScrollTo(width.toFloat().toInt(), animationSpec = TweenSpec(durationMillis = 3000))
                delay(3000)
                scrollState.animateScrollTo(0, animationSpec = TweenSpec(durationMillis = 3000))
                delay(3000)
            }
        }
    }

    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        modifier = modifier.horizontalScroll(scrollState)
    )
}


@Composable
fun EntranceAnimation(content: @Composable () -> Unit) {
    var enter by remember { mutableStateOf(false) }
    val transition = updateTransition(targetState = enter, label = "EntranceAnimation")
    val offsetX by transition.animateDp(
        label = "Offset X"
    ) { state ->
        if (state) {
            5.dp
        } else {
            (-200).dp
        }
    }

    LaunchedEffect(Unit) {
        enter = true
    }

    Box(
        modifier = Modifier.offset(x = offsetX)
    ) {
        content()
    }
}

fun handleLocationName(locationName: String?) {
    if (locationName != null) {
        println("Location name: $locationName")
    } else {
        println("Error fetching location name")
    }
}

fun fetchLocation(context: Context, latitude: Double, longitude: Double, callback: (String?) -> Unit) {
    val geocoder = Geocoder(context, Locale.getDefault())
    try {
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        if (!addresses.isNullOrEmpty()) {
            val areaName = addresses[0].getAddressLine(0)
            if (areaName != null) {
                val trimmedAreaName = areaName.trim()
                if (trimmedAreaName.isNotEmpty()) {
                    Log.d("Location", "Location: $trimmedAreaName")
                    callback(trimmedAreaName)
                    return
                }
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
        Log.e("Location", "Error fetching location", e)
    }
    callback(null)
}

fun fetchAirQuality(
    latitude: Double,
    longitude: Double,
    onResult: (Map<String, Any>) -> Unit
) {
    val call = RetrofitInstance.api.getAirQuality(
        latitude = latitude,
        longitude = longitude,
        hourly = "pm10,pm2_5,carbon_monoxide,nitrogen_dioxide,sulphur_dioxide,ozone",
        timezone = "auto"
    )

    call.enqueue(object : Callback<AirQualityResponse> {
        override fun onResponse(call: Call<AirQualityResponse>, response: Response<AirQualityResponse>) {
            if (response.isSuccessful) {
                val hourlyData = response.body()?.hourly
                val resultMap = mutableMapOf<String, Any>()
                resultMap["pm10"] = hourlyData?.pm10?.firstOrNull() ?: 0.0
                resultMap["pm2_5"] = hourlyData?.pm2_5?.firstOrNull() ?: 0.0
                resultMap["carbon_monoxide"] = hourlyData?.carbon_monoxide?.firstOrNull() ?: 0.0
                resultMap["nitrogen_dioxide"] = hourlyData?.nitrogen_dioxide?.firstOrNull() ?: 0.0
                resultMap["sulphur_dioxide"] = hourlyData?.sulphur_dioxide?.firstOrNull() ?: 0.0
                resultMap["ozone"] = hourlyData?.ozone?.firstOrNull() ?: 0.0

                onResult(resultMap)
            } else {
                Log.e("AirQuality", "Response failed: ${response.message()}")
            }
        }

        override fun onFailure(call: Call<AirQualityResponse>, t: Throwable) {
            Log.e("AirQuality", "Error fetching Air Quality", t)
        }
    })
}

@SuppressLint("ResourceType")
@Composable
fun HomeScreen(navController: NavController) {
//    val weatherViewModel: WeatherViewModel = viewModel()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var aqi by remember { mutableStateOf("--") }
    var aqiCategory by remember { mutableStateOf("Unknown") }

    var pm10 by remember { mutableStateOf("--") }
    var pm25 by remember { mutableStateOf("--") }
    var carbonMonoxide by remember { mutableStateOf("--") }
    var nitrogenDioxide by remember { mutableStateOf("--") }
    var sulphurDioxide by remember { mutableStateOf("--") }
    var ozone by remember { mutableStateOf("--") }

    var latitude by remember { mutableStateOf(0.0) }
    var longitude by remember { mutableStateOf(0.0) }
    var locationName by remember { mutableStateOf<String?>(null) }

    var temperature by remember { mutableStateOf("--") }
    var weatherType by remember { mutableStateOf("--") }
    var formattedSunriseTime by remember { mutableStateOf("--") }
    var formattedSunsetTime by remember { mutableStateOf("--") }
    var humidity by remember { mutableStateOf("--") }
    var uv by remember { mutableStateOf("--") }
    var pressure by remember { mutableStateOf("--") }
    var WindDirection by remember { mutableStateOf("--") }
    var visibility by remember { mutableStateOf("--") }


    var backgroundResource by remember { mutableStateOf(R.drawable.clear_sky) }

    backgroundResource = getBackgroundResourceForWeatherType(weatherType)

    val requestPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    latitude = location.latitude
                    longitude = location.longitude

                    fetchLocation(context, latitude, longitude) { name ->
                        locationName = name ?: "Unknown Location"

                        coroutineScope.launch(Dispatchers.IO) {
                            fetchAirQuality(latitude, longitude) { airQualityData ->
                                pm10 = airQualityData["pm10"].toString()
                                pm25 = airQualityData["pm2_5"].toString()
                                carbonMonoxide = airQualityData["carbon_monoxide"].toString()
                                nitrogenDioxide = airQualityData["nitrogen_dioxide"].toString()
                                sulphurDioxide = airQualityData["sulphur_dioxide"].toString()
                                ozone = airQualityData["ozone"].toString()

                                val aqiValue = pm25.toDoubleOrNull() ?: 0.0
                                aqi = aqiValue.toString()
                                aqiCategory = when (aqiValue) {
                                    in 0.0..50.0 -> "Good"
                                    in 50.1..100.0 -> "Moderate"
                                    in 100.1..150.0 -> "Unhealthy for Sensitive Groups"
                                    in 150.1..200.0 -> "Unhealthy"
                                    in 200.1..300.0 -> "Very Unhealthy"
                                    in 300.1..400.0 -> "Hazardous"
                                    in 400.1..500.0 -> "Severely Hazardous"
                                    else -> "Beyond AQI Scale"
                                }
                            }
                        }
                    }
                } else {
                    locationName = "Unknown Location"
                }
            }
        } else {
            // Handle permission not granted case
        }
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    latitude = location.latitude
                    longitude = location.longitude

                    fetchLocation(context, latitude, longitude) { name ->
                        locationName = name ?: "Unknown Location"

                        coroutineScope.launch(Dispatchers.IO) {
                            fetchAirQuality(latitude, longitude) { airQualityData ->
                                pm10 = airQualityData["pm10"].toString()
                                pm25 = airQualityData["pm2_5"].toString()
                                carbonMonoxide = airQualityData["carbon_monoxide"].toString()
                                nitrogenDioxide = airQualityData["nitrogen_dioxide"].toString()
                                sulphurDioxide = airQualityData["sulphur_dioxide"].toString()
                                ozone = airQualityData["ozone"].toString()

                                val aqiValue = pm25.toDoubleOrNull() ?: 0.0
                                aqi = aqiValue.toString()
                                aqiCategory = when (aqiValue) {
                                    in 0.0..50.0 -> "Good"
                                    in 50.1..100.0 -> "Moderate"
                                    in 100.1..150.0 -> "Unhealthy for Sensitive Groups"
                                    in 150.1..200.0 -> "Unhealthy"
                                    in 200.1..300.0 -> "Very Unhealthy"
                                    in 300.1..400.0 -> "Hazardous"
                                    in 400.1..500.0 -> "Severely Hazardous"
                                    else -> "Beyond AQI Scale"
                                }
                            }
                        }
                    }
                } else {
                    locationName = "Unknown Location"
                }
            }
        } else {
            // Handle permission not granted case
        }
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    latitude = location.latitude
                    longitude = location.longitude

                    fetchLocation(context, latitude, longitude) { name ->
                        locationName = name ?: "Unknown Location"

                        coroutineScope.launch(Dispatchers.IO) {
                            fetchAirQuality(latitude, longitude) { airQualityData ->
                                pm10 = airQualityData["pm10"].toString()
                                pm25 = airQualityData["pm2_5"].toString()
                                carbonMonoxide = airQualityData["carbon_monoxide"].toString()
                                nitrogenDioxide = airQualityData["nitrogen_dioxide"].toString()
                                sulphurDioxide = airQualityData["sulphur_dioxide"].toString()
                                ozone = airQualityData["ozone"].toString()

                                val aqiValue = pm25.toDoubleOrNull() ?: 0.0
                                aqi = aqiValue.toString()
                                aqiCategory = when (aqiValue) {
                                    in 0.0..50.0 -> "Good"
                                    in 50.1..100.0 -> "Moderate"
                                    in 100.1..150.0 -> "Unhealthy for Sensitive Groups"
                                    in 150.1..200.0 -> "Unhealthy"
                                    in 200.1..300.0 -> "Very Unhealthy"
                                    in 300.1..400.0 -> "Hazardous"
                                    in 400.1..500.0 -> "Severely Hazardous"
                                    else -> "Beyond AQI Scale"
                                }
                            }
                        }
                    }
                } else {
                    locationName = "Unknown Location"
                }
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    latitude = location.latitude
                    longitude = location.longitude

                    fetchLocation(context, latitude, longitude) { name ->
                        locationName = name ?: "Unknown Location"

                        fetchWeatherDataByLocationName(locationName!!, context, {
                            temperature = "${it.main.temp}°C"
                            weatherType = it.weather.firstOrNull()?.main ?: "Unknown"
                            formattedSunriseTime = formatTime(it.sys.sunrise * 1000L)
                            formattedSunsetTime = formatTime(it.sys.sunset * 1000L)
                            humidity = "${it.main.humidity}%"
                            uv = "N/A" // Replace with actual UV value if available
                            pressure = "${it.main.pressure} hPa"
                            WindDirection = "${it.wind.deg} M/h" // Replace with actual Wind Direction if available
                            visibility = "${it.visibility} m" // Replace with actual Visibility if available
                             }, {
                            showToast(context, "Error fetching weather data")
                        })


                        coroutineScope.launch(Dispatchers.IO) {
                            fetchAirQuality(latitude, longitude) { airQualityData ->
                                pm10 = airQualityData["pm10"].toString()
                                pm25 = airQualityData["pm2_5"].toString()
                                carbonMonoxide = airQualityData["carbon_monoxide"].toString()
                                nitrogenDioxide = airQualityData["nitrogen_dioxide"].toString()
                                sulphurDioxide = airQualityData["sulphur_dioxide"].toString()
                                ozone = airQualityData["ozone"].toString()


                                val aqiValue = pm25.toDoubleOrNull() ?: 0.0
                                aqi = aqiValue.toString()
                                aqiCategory = when (aqiValue) {
                                    in 0.0..50.0 -> "Good"
                                    in 50.1..100.0 -> "Moderate"
                                    in 100.1..150.0 -> "Unhealthy for Sensitive Groups"
                                    in 150.1..200.0 -> "Unhealthy"
                                    in 200.1..300.0 -> "Very Unhealthy"
                                    in 300.1..400.0 -> "Hazardous"
                                    in 400.1..500.0 -> "Severely Hazardous"
                                    else -> "Beyond AQI Scale"
                                }

                            }
                        }
                    }
                } else {
                    locationName = "Unknown Location"
                }
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }


    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Image(painter = painterResource(id = backgroundResource), contentDescription ="Background" , contentScale = ContentScale.FillBounds)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Location Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                EntranceAnimation {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Outlined.LocationOn,
                            contentDescription = "Location"
                        )
                        MarqueeText(
                            text = locationName ?: "Location",
                            modifier = Modifier
                                .weight(1f)
                                .padding(5.dp)
                        )
                        IconButton(
                            onClick = {
                                // Handle search icon click
                                navController.navigate("SearchScreen")
                            }
                        ) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Search"
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            //weather Icon Display
            EntranceAnimation {
                val iconResource = getWeatherIcons(weatherType)
                Image(
                    painter = painterResource(id = iconResource),
                    contentDescription = "Weather Icon",
                    modifier = Modifier.size(100.dp)
                )
            }

            // Temperature Display
            EntranceAnimation {
                Text(
                    text = temperature,
                    fontSize = 120.sp
                )
            }

            // Weather Type Display
            EntranceAnimation {
                Text(
                    text = weatherType,
                    fontSize = 24.sp
                )
            }

            Spacer(modifier = Modifier.height(80.dp))

            EntranceAnimation {

                SunriseSunsetCard( sunriseTime = formattedSunriseTime, sunsetTime = formattedSunsetTime, sunRiseImage = painterResource(id = R.raw.ic_sun_rise) , sunSetImage = painterResource(id = R.raw.ic_sun_set) )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Weather Info Rows
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(1.dp, 5.dp, 10.dp, 0.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                EntranceAnimation {
                    WeatherItem(
                        title = "Visibility",
                        value = visibility,
                        painter = painterResource(id = R.raw.ic_visibility),
                        backgroundBlur = 10f
                    )
                }

                EntranceAnimation {
                    WeatherItem(
                        title = "Humidity",
                        value = humidity +" HpA",
                        painter = painterResource(id = R.raw.ic_humidity),
                        backgroundBlur = 10f
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(1.dp, 5.dp, 10.dp, 0.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                EntranceAnimation {
                    WeatherItem(
                        title = "Real Feel",
                        value = temperature,
                        painter = painterResource(id = R.raw.ic_thirsty),
                        backgroundBlur = 10f
                    )
                }

                EntranceAnimation {
                    WeatherItem(
                        title = "Wind Speed",
                        value = WindDirection,
                        painter = painterResource(id = R.raw.ic_wind_direction),
                        backgroundBlur = 10f
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(1.dp, 5.dp, 10.dp, 0.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                EntranceAnimation {
                    WeatherItem(
                        title = "Sunset",
                        value = formattedSunsetTime,
                        painter = painterResource(id = R.raw.ic_sun_set),
                        backgroundBlur = 10f
                    )
                }

                EntranceAnimation {
                    WeatherItem(
                        title = "Pressure",
                        value = pressure,
                        painter = painterResource(id = R.raw.ic_pressure),
                        backgroundBlur = 10f
                    )
                }

            }

            EntranceAnimation {
                AqiCard(
                    aqi,
                    aqiCategory,
                    pm10,
                    pm25,
                    carbonMonoxide,
                    nitrogenDioxide,
                    sulphurDioxide,
                    ozone
                )
            }
        }


    }
}


private fun fetchWeatherDataByLocationName(
    locationName: String,
    context: Context,
    onSuccess: (WeatherApp) -> Unit,
    onFailure: () -> Unit
) {
    val geocoder = Geocoder(context, Locale.getDefault())
    try {
        val addresses = geocoder.getFromLocationName(locationName, 1)
        if (!addresses.isNullOrEmpty()) {
            val address = addresses[0]
            fetchWeatherData(address.latitude, address.longitude, onSuccess, onFailure)
        } else {
            onFailure()
        }
    } catch (e: IOException) {
        e.printStackTrace()
        onFailure()
    }
}

private fun fetchWeatherData(
    latitude: Double,
    longitude: Double,
    onSuccess: (WeatherApp) -> Unit,
    onFailure: () -> Unit
) {
    val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("https://api.openweathermap.org/data/2.5/")
        .build()
        .create(ApiInterface::class.java)

    val response = retrofit.getWeatherDataByCoordinates(
        latitude.toString(),
        longitude.toString(),
        Constant.openWeatherMapKey.toString(), // Replace with your actual API key
        "metric"
    )

    response.enqueue(object : Callback<WeatherApp> {
        override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
            val responseBody = response.body()
            if (response.isSuccessful && responseBody != null) {
                onSuccess(responseBody)
            } else {
                onFailure()
            }
        }

        override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
            Log.e("TAG", "Error fetching weather data", t)
            onFailure()
        }
    })
}

fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun getBackgroundResourceForWeatherType(weatherType: String): Int {

    val isDayTime = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) in 6..18


    if (isDayTime) {
        return when (weatherType.lowercase()) {

            "clear" -> R.drawable.clear_sky
            "rain" -> R.drawable.rainy_day
            "drizzle" -> R.drawable.drizzle
            "thunderstorm" -> R.drawable.thunderstorm
            "snow" -> R.drawable.snow
            "mist" -> R.drawable.mist
            "fog" -> R.drawable.fog
            "haze" -> R.drawable.haze
            "smoke" -> R.drawable.smoke
            "dust" -> R.drawable.sandy
            "sand" -> R.drawable.sandy
            "ash" -> R.drawable.ash
            "squall" -> R.drawable.squall
            "tornado" -> R.drawable.tornado
            "clouds" -> R.drawable.cloudy_day
            else -> R.drawable.clear_sky
        }

    } else {
        return when (weatherType.lowercase()) {
            "clear" -> R.drawable.clear_sky
            "rain" -> R.drawable.rainy_day
            "drizzle" -> R.drawable.drizzle
            "thunderstorm" -> R.drawable.thunderstorm
            "snow" -> R.drawable.snow
            "mist" -> R.drawable.mist
            "fog" -> R.drawable.fog
            "haze" -> R.drawable.haze
            "smoke" -> R.drawable.smoke
            "dust" -> R.drawable.sandy
            "sand" -> R.drawable.sandy
            "ash" -> R.drawable.ash
            "squall" -> R.drawable.squall
            "tornado" -> R.drawable.tornado
            "clouds" -> R.drawable.cloudy_day
            else -> R.drawable.clear_sky
        }
    }
}


fun getWeatherIcons(weatherType: String): Int {
    return when (weatherType.lowercase()) {
        "clear" -> R.raw.ic_sun_rise
        "rain" -> R.raw.ic_rain_light
//        "drizzle" -> R.drawable.drizzle
//        "thunderstorm" -> R.drawable.thunderstorm
//        "snow" -> R.drawable.snow
//        "mist" -> R.drawable.mist
//        "fog" -> R.drawable.fog
//        "haze" -> R.drawable.haze
//        "smoke" -> R.drawable.smoke
//        "dust" -> R.drawable.dust
//        "sand" -> R.drawable.sand
//        "ash" -> R.drawable.ash
//        "squall" -> R.drawable.squall
//        "tornado" -> R.drawable.tornado
        "clouds" -> R.raw.cloud
        else -> R.raw.ic_sun_rise
    }
}
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(navController = NavController(LocalContext.current))
}