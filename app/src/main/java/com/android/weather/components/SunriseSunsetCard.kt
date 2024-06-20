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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.weather.R

@Composable
fun SunriseSunsetCard(
    sunriseTime: String,
    sunsetTime: String,
    sunRiseImage: Painter,
    sunSetImage: Painter
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
                modifier = Modifier.padding(16.dp)
                    .border(2.dp, Color.Transparent, RoundedCornerShape(20.dp))
            ) {
                Text(
                    text = "Sunrise & Sunset",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(painter = sunRiseImage, contentDescription = "Sunrise", modifier = Modifier.size(48.dp))
                        Text(
                            text = "Sunrise",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp,
                            )
                        )
                        Text(
                            text = sunriseTime,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                            )
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(painter = sunSetImage, contentDescription = "Sunset", modifier = Modifier.size(48.dp))
                        Text(
                            text = "Sunset",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp,
                            )
                        )
                        Text(
                            text = sunsetTime,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                            )
                        )
                    }
                }
            }
        }
    }
}

@SuppressLint("ResourceType")
@Preview
@Composable
fun SunriseSunsetCardPreview() {
    SunriseSunsetCard(
        sunriseTime = "06:00 AM",
        sunsetTime = "6:00 PM",
        sunRiseImage = painterResource(id = R.raw.ic_sun_rise), // Corrected resource ID
        sunSetImage = painterResource(id = R.raw.ic_sun_set)  // Corrected resource ID
    )
}
