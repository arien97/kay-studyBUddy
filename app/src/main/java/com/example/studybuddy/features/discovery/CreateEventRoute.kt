package com.example.studybuddy.features.discovery

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

val eventList = mutableStateListOf<Event>()

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CreateEventRoute(navController: NavController) {
    var title by remember { mutableStateOf("") }
    var className by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var timeRange by remember { mutableStateOf(8f..20f) } // Default slider range (e.g., 8 AM - 8 PM)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Event", color = MaterialTheme.colorScheme.onPrimary) },
                backgroundColor = MaterialTheme.colorScheme.primary,
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            )
        }
    ) {
        Column(modifier = Modifier.padding(64.dp)) {
            // Input fields
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Event Title") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location / Classroom") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = className,
                onValueChange = { className = it },
                label = { Text("Class") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = date,
                onValueChange = { date = it },
                label = { Text("Date: Month Day, Year") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Time Slider
            SelectTimeSlotSlider(
                timeRange = timeRange,
                onTimeRangeChanged = { timeRange = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Save Event Button
            Button(
                onClick = {
                    // Format the time range as text
                    val timeText = formatTimeRange(timeRange)

                    // Add the new event to the global list
                    eventList.add(
                        Event(
                            id = (eventList.size + 1).toString(),
                            title = title,
                            className = className,
                            startTime = timeRange.start,
                            endTime = timeRange.endInclusive,
                            time = timeText, // Use formatted time text
                            date = date,
                            location = location,
                            description = description,
                            postedBy = "User"
                        )
                    )
                    // Navigate back to the previous screen
                    navController.navigateUp()
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Save Event")
            }
        }
    }
}

@Composable
fun SelectTimeSlotSlider(
    timeRange: ClosedFloatingPointRange<Float>,
    onTimeRangeChanged: (ClosedFloatingPointRange<Float>) -> Unit
) {
    val timeLabels = (0..24).map { i ->
        when {
            i == 0 -> "12 AM"
            i < 12 -> "$i AM"
            i == 12 -> "12 PM"
            else -> "${i - 12} PM"
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        // Display the selected time range as text
        Text(
            text = "Timeframe: ${timeLabels[timeRange.start.toInt()]} - ${timeLabels[timeRange.endInclusive.toInt()]}",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Time slider
        RangeSlider(
            value = timeRange,
            onValueChange = onTimeRangeChanged,
            valueRange = 0f..24f,
            steps = 23,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

fun formatTimeRange(timeRange: ClosedFloatingPointRange<Float>): String {
    fun formatTime(hour: Float): String {
        val hourInt = hour.toInt()
        return when {
            hourInt == 0 -> "12 AM"
            hourInt < 12 -> "$hourInt AM"
            hourInt == 12 -> "12 PM"
            else -> "${hourInt - 12} PM"
        }
    }

    return "${formatTime(timeRange.start)} - ${formatTime(timeRange.endInclusive)}"
}
