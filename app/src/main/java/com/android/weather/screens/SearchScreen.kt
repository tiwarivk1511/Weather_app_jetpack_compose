package com.android.weather.screens

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.android.weather.R
import com.android.weather.components.AqiCard
import com.android.weather.components.SunriseSunsetCard
import com.android.weather.components.WeatherItem
import com.android.weather.screens.Constant.latitude
import com.android.weather.screens.Constant.longitude
import com.android.whether.ApiInterface
import com.android.whether.WeatherApp
import com.google.android.libraries.places.api.Places
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("ResourceType")
@Composable
fun SearchScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    var latitude by remember { mutableDoubleStateOf(0.0) }
    var longitude by remember { mutableDoubleStateOf(0.0) }
    var locationName by remember { mutableStateOf<String?>(null) }

    var temperature by remember { mutableStateOf("--") }
    var weatherType by remember { mutableStateOf("--") }
    var formattedSunriseTime by remember { mutableStateOf("--") }
    var formattedSunsetTime by remember { mutableStateOf("--") }
    var humidity by remember { mutableStateOf("--") }
    var uv by remember { mutableStateOf("--") }
    var pressure by remember { mutableStateOf("--") }
    var windDirection by remember { mutableStateOf("--") }
    var visibility by remember { mutableStateOf("--") }

    var backgroundResource by remember { mutableIntStateOf(R.drawable.clear_sky) }


    backgroundResource = getBackgroundResourceForWeatherType(weatherType)
    // Initialize Google Places API
    Places.initialize(context, Constant.key)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

// set the background
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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyLarge,colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Black, // Change focused border color here
                        unfocusedBorderColor = Color.Black, // Change unfocused border color here
                        focusedLabelColor = Color.Black, // Change focused label color (optional)
                        cursorColor = Color.Black // Change cursor color (optional)
                        // ... add more color customizations as needed
                    ),
                    onValueChange = { searchQuery = it },
                    label = { Text("Search Location",
                        color = Color.Black,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(end = 8.dp))},
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        keyboardController?.hide()
                        fetchWeatherDataByLocationName(searchQuery, context, {
                            temperature = "${it.main.temp}°C"
                            weatherType = it.weather.firstOrNull()?.main ?: "Unknown"
                            windDirection = "${it.wind.deg} M/h"
                            visibility = "${it.visibility} m"
                            formattedSunriseTime = formatTime(it.sys.sunrise * 1000L)
                            formattedSunsetTime = formatTime(it.sys.sunset * 1000L)
                            humidity = "${it.main.humidity}%"
                            uv = "N/A" // Replace with actual UV value if available
                            pressure = "${it.main.pressure} hPa"
                            locationName = it.name
                        }, {
                            showToast(context, "Error fetching weather data")
                        })
                        searchQuery = ""
                    })
                )


                IconButton(
                    onClick = {
                        fetchWeatherDataByLocationName(searchQuery, context, {
                            locationName =searchQuery
                            temperature = "${it.main.temp}°C"
                            temperature = "${it.main.temp}°C"
                            weatherType = it.weather.firstOrNull()?.main ?: "Unknown"
                            windDirection = "${it.wind.deg} M/h"
                            formattedSunriseTime = formatTime(it.sys.sunrise * 1000L)
                            formattedSunsetTime = formatTime(it.sys.sunset * 1000L)
                            humidity = "${it.main.humidity}%"
                            uv = "N/A" // Replace with actual UV value if available
                            pressure = "${it.main.pressure} hPa"
                            visibility = "${it.visibility} m" // Replace with actual Visibility if available


                        }, {
                            showToast(context, "Error fetching weather data")
                        })
                        searchQuery = ""
                    }
                ) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row {
                Icon(Icons.Outlined.LocationOn, contentDescription = "Location Icon" )

                Text(
                    text = locationName?:"--",
                    fontSize = 24.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = temperature, fontSize = 120.sp)
            Text(text = weatherType, fontSize = 24.sp)

            Spacer(modifier = Modifier.height(80.dp))

            EntranceAnimation {

                SunriseSunsetCard(
                    sunriseTime = formattedSunriseTime,
                    sunsetTime = formattedSunsetTime,
                    sunRiseImage = painterResource(id = R.raw.ic_sun_rise),
                    sunSetImage = painterResource(id = R.raw.ic_sun_set)
                )

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
                        value = "$humidity HpA",
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
                        value = windDirection,
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

            Spacer(modifier = Modifier.height(16.dp))
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
            latitude = address.latitude
            longitude = address.longitude
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

    val call = retrofit.getWeatherDataByCoordinates(
        latitude.toString(),
        longitude.toString(),
        Constant.openWeatherMapKey, // Ensure your API key is stored as a String in Constant
        "metric"
    )

    call.enqueue(object : Callback<WeatherApp> {
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
