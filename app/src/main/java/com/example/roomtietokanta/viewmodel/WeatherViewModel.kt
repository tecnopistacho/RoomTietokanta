package com.example.roomtietokanta.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomtietokanta.data.model.WeatherEntity
import com.example.roomtietokanta.data.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class WeatherUiState(
    val city: String = "",
    val temperature: Double? = null,
    val description: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val lastUpdated: Long? = null
)

class WeatherViewModel(
    private val repository: WeatherRepository,
    private val apiFetcher: suspend (String) -> WeatherEntity
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState

    private val _searchHistory = MutableStateFlow<List<String>>(emptyList())
    val searchHistory: StateFlow<List<String>> = _searchHistory.asStateFlow()

    init{
        // Collect search history from Room
        viewModelScope.launch {
            repository.getSearchhistory().collect { cities ->
                _searchHistory.value = cities
            }
        }
    }

    fun updateCity(city: String) {
        _uiState.value = _uiState.value.copy(
            city = city,
            temperature = null,
            description = "",
            error = null
        )
    }

    fun selectCity(city: String) {
        updateCity(city)
        fetchWeather()
    }

    fun fetchWeather() {
        val city = _uiState.value.city
        if (city.isBlank()) return

        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            repository.getWeather(city).collectLatest { cached ->
                val now = System.currentTimeMillis()
                val thirtyMinutes = 30 * 60 * 1000
                // If the data is in Room and it was fetched less than 30 min ago,
                // it will show the catched data instead of calling API again

                // If the data is older than 30 min or not present
                // it calls API and saves it to Room

                if (cached != null && now - cached.timestamp < thirtyMinutes) {
                    _uiState.value = WeatherUiState(
                        city = cached.city,
                        temperature = cached.temperature,
                        description = cached.description,
                        lastUpdated = cached.timestamp,
                        isLoading = false
                    )
                } else {
                    try {
                        val fresh = apiFetcher(city)
                        if (fresh.temperature != null) {
                            repository.saveWeather(fresh)
                            _uiState.value = WeatherUiState(
                                city = fresh.city,
                                temperature = fresh.temperature,
                                description = fresh.description,
                                lastUpdated = fresh.timestamp,
                                isLoading = false
                            )

                            // Only add to search history if fetch was successful
                            if (!_searchHistory.value.contains(fresh.city)) {
                                _searchHistory.value = listOf(fresh.city) + _searchHistory.value
                            }
                        } else {
                            _uiState.value = _uiState.value.copy(
                                error = "Invalid city",
                                isLoading = false
                            )
                        }
                    } catch (e: Exception) {
                        val errorMessage = when (e) {
                            is retrofit2.HttpException -> {
                                if (e.code() == 404) {
                                    "City not found. Please check the spelling."
                                } else {
                                    "Server error (${e.code()}). Please try again."
                                }
                            }

                            is java.io.IOException -> {
                                "No internet connection. Please check your network"
                            }
                            else -> {
                                "Something went wrong. Please try again."
                            }
                        }

                        _uiState.value = _uiState.value.copy(
                            error = errorMessage,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }
}