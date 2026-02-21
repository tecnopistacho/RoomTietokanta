package com.example.roomtietokanta.data.repository

import com.example.roomtietokanta.data.local.WeatherDao
import com.example.roomtietokanta.data.model.WeatherEntity
import kotlinx.coroutines.flow.Flow

class WeatherRepository(private val weatherDao: WeatherDao) {
    fun getWeather(city: String): Flow<WeatherEntity?> {
        return weatherDao.getWeather(city)
    }

    suspend fun saveWeather(weather: WeatherEntity) {
        weatherDao.insertWeather(weather)
    }

    fun getSearchhistory(): Flow<List<String>> {
        return weatherDao.getAllCities()
    }
}