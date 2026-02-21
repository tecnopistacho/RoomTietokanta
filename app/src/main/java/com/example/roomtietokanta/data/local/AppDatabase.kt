package com.example.roomtietokanta.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.roomtietokanta.data.model.WeatherEntity

@Database(
    entities = [WeatherEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun weatherDao(): WeatherDao
}