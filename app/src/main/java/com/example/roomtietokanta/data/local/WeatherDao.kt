package com.example.roomtietokanta.data.local

import androidx.room.*
import com.example.roomtietokanta.data.model.WeatherEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: WeatherEntity)

    @Query("SELECT * FROM weather WHERE city = :city LIMIT 1")
    fun getWeather(city: String): Flow<WeatherEntity?>

    @Query("SELECT DISTINCT city FROM weather ORDER BY timestamp DESC")
    fun getAllCities(): Flow<List<String>>
}