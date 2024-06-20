package com.android.weather.screens

//create a splash screen using jetpack compose
import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.android.weather.R
import kotlinx.coroutines.delay


@SuppressLint("ResourceType")
@Composable
fun SplashImage() {
    Image(
        painter = painterResource(R.raw.ic_weather_app),
        contentDescription = "App Icon",
        modifier = Modifier.size(128.dp)
    )
}


@Composable
fun SplashScreen(navController: NavController) {
    var navigatedToHomeScreen by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(3000) // Wait for 3 seconds
        if (!navigatedToHomeScreen) {
            navController.navigate("HomeScreen" ) // Replace "HomeScreen" with your destination
            navigatedToHomeScreen = true
        }
    }
    // Gradient background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.DarkGray, Color.Gray, Color.Blue),
                    startY = 0f,
                    endY = 600f
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SplashImage()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Weather", fontSize = 24.sp, color = Color.White,
                modifier = Modifier.padding(16.dp)
                    .animateContentSize(animationSpec = tween(durationMillis = 300)))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    SplashScreen(navController = NavController(LocalContext.current))
}