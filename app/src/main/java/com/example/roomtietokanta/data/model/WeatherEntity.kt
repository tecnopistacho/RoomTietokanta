package com.example.roomtietokanta.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather")
data class WeatherEntity(
    @PrimaryKey val city: String,
    val temperature: Double,
    val description: String,
    val timestamp: Long
)