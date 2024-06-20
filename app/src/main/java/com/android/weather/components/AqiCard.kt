package com.android.weather.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.weather.R

@SuppressLint("ResourceType")
@Composable
fun AqiCard(
    aqiValue: String,
    aqiStatusText: String,
    pm10: String,
    pm25: String,
    carbonMonoxide: String,
    nitrogenDioxide: String,
    sulphurDioxide: String,
    ozone: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(2.dp, Color.LightGray, RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .graphicsLayer {
                shape = RoundedCornerShape(16.dp)
                clip = true
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // Blurred background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
                    .blur(16.dp)
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.raw.ic_mask),
                        contentDescription = "AQI Icon",
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "AQI: $aqiValue",
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Status: $aqiStatusText",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }
                }

                Text(
                    text = "Air Quality Parameters",
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.secondary
                )

                Spacer(modifier = Modifier.height(18.dp))
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.padding(bottom = 8.dp).fillMaxWidth()
                ){
                    AirQualityParameter(text = "PM10", value = pm10)
                    Spacer(modifier = Modifier.width(16.dp))
                    AirQualityParameter(text = "PM2.5", value = pm25)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.padding(bottom = 8.dp).fillMaxWidth()
                ){
                    AirQualityParameter(text = "Carbon Monoxide", value = carbonMonoxide)
                    Spacer(modifier = Modifier.width(5.dp))
                    AirQualityParameter(text = "Nitrogen Dioxide", value = nitrogenDioxide)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.padding(bottom = 8.dp).fillMaxWidth()
                ){
                    AirQualityParameter(text = "Sulphur Dioxide", value = sulphurDioxide)

                    AirQualityParameter(text = "Ozone", value = ozone)
                }
                Spacer(modifier = Modifier.height(8.dp))

            }
        }
    }
}


@Composable
@Preview(showBackground = true)
fun AqiCardPreview() {
    AqiCard(
        aqiValue = "101",
        aqiStatusText = "Good",
        pm10 = "10",
        pm25 = "10",
        carbonMonoxide = "10",
        nitrogenDioxide = "10",
        sulphurDioxide = "10",
        ozone = "10"
    )
}

@Composable
private fun AirQualityParameter(text: String, value: String) {
    Column {
        Text(
            text = "$text: $value",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            modifier = Modifier.padding(vertical = 4.dp).align(Alignment.CenterHorizontally)
        )
    }
}
