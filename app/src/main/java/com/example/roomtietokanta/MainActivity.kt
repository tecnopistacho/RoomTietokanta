package com.example.roomtietokanta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.roomtietokanta.data.local.AppDatabase
import com.example.roomtietokanta.data.repository.WeatherRepository
import com.example.roomtietokanta.ui.WeatherScreen
import com.example.roomtietokanta.ui.theme.RoomTietokantaTheme
import com.example.roomtietokanta.viewmodel.WeatherViewModel
import com.example.roomtietokanta.data.model.WeatherEntity
import com.example.roomtietokanta.data.model.WeatherResponse
import com.example.roomtietokanta.data.remote.RetrofitInstance

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // ⚠️ Temporary: delete old DB for development
        applicationContext.deleteDatabase("weather_db")

        // 1️⃣ Create Room database
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "weather_db"
        )
            .fallbackToDestructiveMigration()
            .build()

        // 2️⃣ Create Repository
        val repository = WeatherRepository(db.weatherDao())

        // 3️⃣ Create ViewModel with factory
        val viewModel = ViewModelProvider(this,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return WeatherViewModel(
                        repository = repository,
                        apiFetcher = { city ->
                            fetchWeatherFromApi(city)
                        }
                    ) as T
                }
            }
        )[WeatherViewModel::class.java]

        // 4️⃣ Set Compose content
        setContent {
            RoomTietokantaTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    WeatherScreen(
                        modifier = Modifier.padding(innerPadding),
                        weatherViewModel = viewModel
                    )
                }
            }
        }
    }

    // ✅ Fetch API and convert to WeatherEntity for Room
    private suspend fun fetchWeatherFromApi(city: String): WeatherEntity {
        val result: WeatherResponse = RetrofitInstance.api.getWeather(
            city = city,
            apiKey = BuildConfig.OPEN_WEATHER_API_KEY
        )
        return WeatherEntity(
            city = result.name,
            temperature = result.main.temp,
            description = result.weather.firstOrNull()?.description ?: "",
            timestamp = System.currentTimeMillis()
        )
    }
}