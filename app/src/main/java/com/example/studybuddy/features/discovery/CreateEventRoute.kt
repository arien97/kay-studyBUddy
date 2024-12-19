@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.studybuddy.features.discovery

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.studybuddy.utils.DateUtils
import java.time.LocalDate
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng

@Composable
fun CreateEventRoute(
    navController: NavController,
    viewModel: CreateEventViewModel = hiltViewModel()
) {
    val event by viewModel.event.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    // Maps related state
    val initialPosition = LatLng(42.3505, -71.1054) // CAS
    var markerPosition by remember { mutableStateOf(initialPosition) }
    val markerState = remember { MarkerState(position = initialPosition) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(markerState.position, 12f)
    }

    if (showDialog) {
        EditDatePicker(
            {
                viewModel.edit { copy(date = it?.let(DateUtils::dateToString)) }
                showDialog = false
            },
            { showDialog = false },
            event.date,
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Event", color = MaterialTheme.colorScheme.onPrimary) },
                backgroundColor = MaterialTheme.colorScheme.primary,
            )
        }, content = { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)  // Add scaffold padding
                    .verticalScroll(rememberScrollState())  // Make content scrollable
                    .padding(horizontal = 64.dp)  // Keep your horizontal padding

            ) {
                // Input fields
                OutlinedTextField(
                    value = event.title.orEmpty(),
                    onValueChange = { viewModel.edit { copy(title = it) } },
                    label = { Text("Event Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = event.location.orEmpty(),
                    onValueChange = { viewModel.edit { copy(location = it) } },
                    label = { Text("Location / Classroom") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = event.course.orEmpty(),
                    onValueChange = { viewModel.edit { copy(course = it) } },
                    label = { Text("Course") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = event.date.orEmpty(),
                    onValueChange = { },
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                    label = { Text("Date: Month Day, Year") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDialog = true },
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = event.description.orEmpty(),
                    onValueChange = { viewModel.edit { copy(description = it) } },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Time Slider
                SelectTimeSlotSlider(
                    timeRange = event.startTime..event.endTime,
                    onTimeRangeChanged = {
                        viewModel.edit { copy(startTime = it.start, endTime = it.endInclusive) }
                    },
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Google Maps Component
                Text("Choose Event Location:", style = MaterialTheme.typography.bodyLarge)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState
                    ) {
                        Marker(
                            state = markerState,
                            draggable = true,
                            onClick = {
                                // Update the marker position in the ViewModel
                                viewModel.edit {
                                    copy(
                                        latitude = markerState.position.latitude,
                                        longitude = markerState.position.longitude
                                    )
                                }
                                false
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Save Event Button
                val isLoading by viewModel.isLoading.collectAsState()
                val isValid by viewModel.isValid.collectAsState(false)
                if (!isLoading) {
                    Button(
                        onClick = {
                            // Update location coordinates before saving
                            viewModel.edit {
                                copy(
                                    latitude = markerState.position.latitude,
                                    longitude = markerState.position.longitude
                                )
                            }
                            viewModel.createEvent()
                        },
                        enabled = isValid,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Text("Post Event")
                    }
                } else {
                    CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
                }
            }
        }
    )
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
        Text(
            text = "Timeframe: ${timeLabels[timeRange.start.toInt()]} - ${timeLabels[timeRange.endInclusive.toInt()]}",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

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

@Composable
private fun EditDatePicker(
    dateSelected: (LocalDate?) -> Unit,
    dismiss: () -> Unit,
    selected: String?,
) {
    val dateState = rememberDatePickerState()
    dateState.selectedDateMillis =
        selected?.let(DateUtils::stringToDate)
            ?.let(DateUtils::convertLocalDateToMillis)
    DatePickerDialog(
        onDismissRequest = dismiss,
        confirmButton = {
            Button(onClick = {
                dateSelected.invoke(dateState.selectedDateMillis?.let(DateUtils::convertMillisToLocalDate))
            }) { Text(text = "OK") }
        },
        dismissButton = { Button(onClick = dismiss) { Text(text = "Cancel") } }
    ) {
        DatePicker(
            state = dateState,
            showModeToggle = true
        )
    }
}