package com.example.studybuddy.features.discovery

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.studybuddy.domain.Event
import com.example.studybuddy.components.EventPreviewCard


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DiscoveryRoute(navController: NavController, viewModel: DiscoveryViewModel = hiltViewModel()) {
    var selectedFilter by remember { mutableStateOf<String?>(null) }
    var timeRange by remember { mutableStateOf(4f..20f) }
    val courses by viewModel.courses.collectAsState()
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Home Page",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = "",
            onValueChange = { /* Handle search HERE !!*/ },
            label = { Text("Search events") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Class Filter Buttons
        ClassFilterButtons(selectedFilter, courses.toList()) { selected ->
            selectedFilter = if (selectedFilter == selected) null else selected
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Time Slot Slider
        TimeSlotSlider(
            timeRange = timeRange,
            onTimeRangeChanged = { timeRange = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        val events by viewModel.events.collectAsState()
        LazyColumn {
            val filteredEvents = events.filter { event ->
                val eventStart = event.startTime ?: 0f
                val eventEnd = event.endTime ?: 0f
                val sliderStart = timeRange.start
                val sliderEnd = timeRange.endInclusive

                val isWithinClassFilter =
                    selectedFilter.isNullOrEmpty() || event.course == selectedFilter
                val isWithinTimeFilter = eventStart < sliderEnd && eventEnd > sliderStart

                isWithinClassFilter && isWithinTimeFilter
            }

            items(filteredEvents) { event ->
                EventPreviewCard(
                    event = event,
                    onClick = { navController.navigate("event_details/${event.id}") }
                )
            }
        }
    }
}

@Composable
fun ClassFilterButtons(
    selectedFilter: String?,
    courses: List<String>,
    onFilterSelected: (String) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(courses) { className ->
            FilterButton(
                className = className,
                isSelected = selectedFilter == className,
                onClick = { onFilterSelected(className) }
            )
        }
    }
}

@Composable
fun FilterButton(className: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer,
            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer
        ),
        modifier = Modifier.height(40.dp)
    ) {
        Text(text = className, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun TimeSlotSlider(
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

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "12:00 AM", style = MaterialTheme.typography.bodySmall)
            Text(text = "11:59 PM", style = MaterialTheme.typography.bodySmall)
        }
    }
}
