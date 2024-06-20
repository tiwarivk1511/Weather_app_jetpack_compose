package com.android.weather.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.weather.R

@SuppressLint("ResourceType")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherItem(
    title: String,
    painter: Painter,
    value: String,
    backgroundBlur: Float, // Adjust the blur radius as needed
) {
    Box(
        modifier = Modifier
            .padding(2.dp)
            .size(145.dp)
            .clip(RoundedCornerShape(20.dp)) // Set corner radius to 20dp
            .background(Color.Transparent) // Ensure the Box background is transparent
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {
        // Blurred background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .blur(backgroundBlur.dp)
                .clip(RoundedCornerShape(20.dp))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(1.dp)
                .clip(RoundedCornerShape(20.dp))
                .border(2.dp, Color.LightGray, RoundedCornerShape(20.dp)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp)) // Increased spacer height

            Image(
                painter = painter,
                contentDescription = "Data Icon",
                modifier = Modifier.size(48.dp) // Increased image size
            )

            Spacer(modifier = Modifier.height(8.dp)) // Increased spacer height

            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp, // Increased font size
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp)) // Increased spacer height

            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp, // Increased font size
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    WeatherItem(
        title = "Humidity",
        value = "85%",
        painter = painterResource(id = R.drawable.ic_notification),
        backgroundBlur = 10f
    )
}
