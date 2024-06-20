package com.android.weather

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.android.weather.screens.HomeScreen
import com.android.weather.screens.SearchScreen
import com.android.weather.screens.SplashScreen
import com.android.weather.ui.theme.WeatherTheme

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            WeatherTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    MyApp(navController)
                }
            }
        }
    }
}

@Composable
fun MyApp(navController: NavHostController) {
    NavHost(navController, startDestination = "splash_screen") {
        composable("splash_screen") {
            SplashScreen(navController)
        }

        composable("HomeScreen") {
            HomeScreen(navController)
        }

        composable("SearchScreen") {
            SearchScreen(navController)
        }

    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMyApp() {
    WeatherTheme {
        val navController = rememberNavController()
        MyApp(navController)
    }
}
