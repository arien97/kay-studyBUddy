package com.example.studybuddy.features.profile

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun ProfileRoute(
    signOut: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(Unit) {
        viewModel.signOut.flowWithLifecycle(lifecycle = lifecycle).collect { signOut() }
    }

    Column(
        modifier
            .fillMaxSize()
    ) {
        Text(
            text = "Profile",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(16.dp)
        )
//
//        Image(
//            imageVector = Icons.Default.Person,
//            contentDescription = null,
//            contentScale = androidx.compose.ui.layout.ContentScale.FillWidth,
//            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
//            modifier = Modifier.fillMaxWidth(0.5f),
//        )
//
//        val isLoading by viewModel.isLoading.collectAsState()
//        if (!isLoading) {
//            Button(
//                content = { Text("Sign Out") },
//                onClick = { viewModel.signOut() },
//                modifier = Modifier.fillMaxWidth(),
//                shape = RoundedCornerShape(10.dp),
//            )
//        } else {
//            CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
//        }

        ProfileScreen(navController = rememberNavController())
    }
}

@Composable
fun ProfileScreen(navController: NavHostController) {
    val context = LocalContext.current
    val showPopup = remember { mutableStateOf(false) }
    val showAllEvents = remember { mutableStateOf(false) }
    val events = remember {
        mutableStateOf(
            listOf(
                Event("Calculus hw3", "Wednesday", "6-9pm", "User1"),
                Event("Physics Lab", "Thursday", "2-4pm", "User2"),
                Event("Group Study", "Friday", "5-7pm", "User3"),
                Event("Chemistry Review", "Saturday", "3-5pm", "User4")
            )
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(bottom = 80.dp), // Add padding to ensure the button is visible
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                // Profile Picture and Username
                CircleAvatar()
                Text(text = "Edit", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Username", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                // Calendar
                CalendarView(showPopup)
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Your Events", style = MaterialTheme.typography.titleMedium)
            }

            // Events Section
            val eventsToShow = if (showAllEvents.value) events.value else events.value.take(3)
            items(eventsToShow) { event ->
                EventItem(event)
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                // Show More/Show Less Button
                if (events.value.size > 3) {
                    Button(
                        onClick = { showAllEvents.value = !showAllEvents.value },
                        colors = ButtonDefaults.buttonColors(Color(0xFF1E90FF))
                    ) {
                        Text(if (showAllEvents.value) "Show Less" else "Show More", color = Color.White)
                    }
                }
            }
        }

        // Settings Button
//        IconButton(
//            onClick = { navController.navigate("settings") },
//            modifier = Modifier.align(Alignment.TopEnd)
//        ) {
//            Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings", tint = Color(0xFF1E90FF)) // Specific blue color
//        }

        // Popup Message
        if (showPopup.value) {
            AlertDialog(
                onDismissRequest = { showPopup.value = false },
                title = { Text(text = "Events") },
                text = { Text("You have no events on this day, go to Home to create an event.") },
                confirmButton = {
                    Button(onClick = { showPopup.value = false }, colors = ButtonDefaults.buttonColors(Color(0xFF1E90FF))) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

@Composable
fun CircleAvatar() {
    Box(
        modifier = Modifier
            .size(100.dp)
            .background(Color(0xFF1E90FF), shape = CircleShape) // Specific blue color
    )
}

@Composable
fun CalendarView(showPopup: MutableState<Boolean>) {
    val currentMonth = remember { mutableStateOf(YearMonth.now()) }
    val monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { currentMonth.value = currentMonth.value.minusMonths(1) }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Previous Month")
            }
            Text(
                text = currentMonth.value.format(monthFormatter),
                style = MaterialTheme.typography.titleMedium
            )
            IconButton(onClick = { currentMonth.value = currentMonth.value.plusMonths(1) }) {
                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Next Month")
            }
        }
        CalendarGrid(currentMonth.value, showPopup)
    }
}

@Composable
fun CalendarGrid(currentMonth: YearMonth, showPopup: MutableState<Boolean>) {
    val firstDayOfMonth = currentMonth.atDay(1)
    val dayOfWeek = firstDayOfMonth.dayOfWeek.value % 7 // Adjust to make Sunday = 0
    val daysInMonth = currentMonth.lengthOfMonth()
    val days = (1..daysInMonth).toList()
    val calendarDays = MutableList(42) { "" }

    for (i in days.indices) {
        calendarDays[dayOfWeek + i] = days[i].toString()
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        for (week in 0 until 6) {
            Row {
                for (day in 0 until 7) {
                    val index = week * 7 + day
                    val dayText = calendarDays.getOrNull(index) ?: ""
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .size(40.dp) // Adjusted size to fit all dates
                            .background(if (dayText.isEmpty()) Color.Transparent else Color.White) // Light blue color for empty days
                            .clickable(enabled = dayText.isNotEmpty()) { showPopup.value = true }
                    ) {
                        Text(
                            text = dayText,
                            modifier = Modifier.align(Alignment.Center),
                            color = if (dayText.isEmpty()) Color.Gray else Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EventItem(event: Event) {
    Spacer(modifier = Modifier.height(8.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFE0F7FA)) // Super light blue background
            .padding(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(text = event.title, style = MaterialTheme.typography.titleMedium)
                Text(text = "${event.day} ${event.timeRange}")
                Text(text = event.username)
            }
        }
    }
}

data class Event(
    val title: String,
    val day: String,
    val timeRange: String,
    val username: String
)