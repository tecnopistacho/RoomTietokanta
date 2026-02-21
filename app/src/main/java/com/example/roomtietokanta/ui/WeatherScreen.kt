package com.example.roomtietokanta.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.roomtietokanta.viewmodel.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn

@Composable
fun WeatherScreen(
    modifier: Modifier = Modifier,
    weatherViewModel: WeatherViewModel = viewModel()
) {
    val uiState by weatherViewModel.uiState.collectAsState()
    val searchHistory by weatherViewModel.searchHistory.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Input for city
        OutlinedTextField(
            value = uiState.city,
            onValueChange = { weatherViewModel.updateCity(it) },
            label = { Text("City") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Check Weather button
        Button(
            onClick = { weatherViewModel.fetchWeather() },
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState.city.isNotBlank() && !uiState.isLoading
        ) {
            Text("Check Weather")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search history
        if (searchHistory.isNotEmpty()) {
            Text("Recent searches:", style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Column {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    searchHistory.forEach { cityName ->
                        AssistChip(
                            onClick = { weatherViewModel.selectCity(cityName) },
                            label = { Text(text = cityName) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Loading indicator
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Fetching weather for ${uiState.city}...")

                }
            }
        }

        // Error message
        uiState.error?.let { errorMsg ->
            Text(
                text = errorMsg,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        // Weather info
        if (!uiState.isLoading && uiState.temperature != null) {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                Text("In the city of ${uiState.city}")
                Text("The temperature is ${uiState.temperature} °C")
                Text("And the weather is ${uiState.description}")

                // Last updated timestamp
                uiState.lastUpdated?.let { ts ->
                    Text(
                        "Last updated: ${SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(ts))}",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}