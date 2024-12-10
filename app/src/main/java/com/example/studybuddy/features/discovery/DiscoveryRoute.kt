package com.example.studybuddy.features.discovery

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DiscoveryRoute(navController: NavController) {
    Column(modifier = Modifier.padding(16.dp)) {
            // Title
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

            // Buttons sliders etc etc
            Spacer(modifier = Modifier.height(16.dp))
            ClassFilterButtons()
            Spacer(modifier = Modifier.height(16.dp))
            TimeSlotSlider()
            Spacer(modifier = Modifier.height(16.dp))

            // Event List
            LazyColumn {
                items(sampleEvents) { event ->
                    EventCard(event = event) {
                        navController.navigate("event_details/${event.id}")
                    }
                }
            }
        }
    }



@Composable
fun ClassFilterButtons() {
    // Sample class filter data ADD USER ONES LATER
    val classFilters = listOf("CS 501", "CS 460", "CS 350", "CS 506")
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(classFilters) { className ->
            FilterButton(className)
        }
    }
}

@Composable
fun FilterButton(className: String) {
    Button(
        onClick = { /* Handle class filter click */ },
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        modifier = Modifier.height(40.dp)
    ) {
        Text(text = className, style = MaterialTheme.typography.bodyMedium)
    }
}


@Composable
fun TimeSlotSlider() {
    // State to hold the start and end values of the range slider
    var timeRange by remember { mutableStateOf(4f..20f) }

    val timeLabels = (0..24).map { i ->
        when {
            i == 0 -> "12 AM"
            i < 12 -> "$i AM"
            i == 12 -> "12 PM"
            else -> "${i - 12} PM"
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        // Display the currently selected time range
        Text(
            text = "Timeframe: ${timeLabels[timeRange.start.toInt()]} - ${timeLabels[timeRange.endInclusive.toInt()]}",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        RangeSlider(
            value = timeRange,
            onValueChange = { timeRange = it },
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


@Composable
fun EventCard(event: Event, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(event.title, style = MaterialTheme.typography.headlineSmall)
                Text(event.className, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                Text(event.time, style = MaterialTheme.typography.bodySmall)
                Text("Posted by: ${event.postedBy}", style = MaterialTheme.typography.labelSmall)
            }
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Go to Event Details"

            )
        }
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun EventDetailsPage(navController: NavController, eventId: String) {
    val event = sampleEvents.find { it.id == eventId }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Event Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) {
        if (event != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Posted By: ",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile Icon",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = event.postedBy,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { /* Add to Calendar logic */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Add to Calendar")
                    }
                    Button(
                        onClick = { /* Message logic */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Message")
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Description:",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = event.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Details",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Location: ${event.location}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Time: ${event.time}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Date: ${event.date}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            Text(
                text = "Event not found",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}


// Sample Events
val sampleEvent1 = Event(
    id = "1",
    title = "CS 501 Final Study",
    className = "CS 501",
    time = "7PM - 11PM",
    date = "Friday Dec 6",
    postedBy = "KAYS",
    location = "CAS 225",
    description = "Group study session to review for the final! Bring some snacks. We will do review questions and go over slides."
)

val sampleEvents: List<Event> = listOf(sampleEvent1)

data class Event(
    val id: String,
    val title: String,
    val className: String,
    val time: String,
    val date: String,
    val postedBy: String,
    val location: String,
    val description: String
)

