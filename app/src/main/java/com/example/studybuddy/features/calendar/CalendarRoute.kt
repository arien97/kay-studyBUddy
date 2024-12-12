package com.example.studybuddy.features.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*

data class Event(
    val title: String,
    val day: String,
    val date: String,
    val timeRange: String,
    val username: String,
    val className: String
)

@Composable
fun CalendarRoute(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = "calendar") {
        composable("calendar") {
            CalendarScreen(navController)
        }
        composable("event_details/{date}") { backStackEntry ->
            val date = backStackEntry.arguments?.getString("date")
            EventDetailsScreen(navController, date ?: "")
        }
    }
}

@Composable
fun CalendarScreen(navController: NavHostController) {
    val showPopup = remember { mutableStateOf(false) }
    val events = listOf(
        Event("Calculus hw3", "Wednesday", "12/11/24", "6-9pm", "User1", "MA491"),
        Event("Physics Lab", "Thursday", "12/12/24", "2-4pm", "User2", "PH211"),
        Event("Group Study", "Friday", "12/13/24", "5-7pm", "User3", "CS501"),
        Event("Data Science Review", "Saturday", "12/14/24", "3-5pm", "User4", "DS201"),
        Event("Finals Review", "Tuesday", "12/17/24", "1-3pm", "User5", "CS501"),
        Event("Finals Review 2", "Tuesday", "12/17/24", "5-7pm", "User5", "CS501"),

    )
    Column {
        Text(
            text = "Calendar",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(16.dp)
        )
        CalendarView(showPopup, navController, events)
    }

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

@Composable
fun CalendarView(showPopup: MutableState<Boolean>, navController: NavHostController, events: List<Event>) {
    val currentMonth = remember { mutableStateOf(YearMonth.now()) }
    val monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.fillMaxSize()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { currentMonth.value = currentMonth.value.minusMonths(1) }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Previous Month")
            }
            Text(
                text = currentMonth.value.format(monthFormatter),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            IconButton(onClick = { currentMonth.value = currentMonth.value.plusMonths(1) }) {
                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Next Month")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        DaysOfWeekHeader()
        CalendarGrid(currentMonth.value, showPopup, navController, events)
    }
}

@Composable
fun DaysOfWeekHeader() {
    val daysOfWeek = listOf("S", "M", "T", "W", "T", "F", "S")
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        daysOfWeek.forEach { day ->
            Text(
                text = day,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun CalendarGrid(currentMonth: YearMonth, showPopup: MutableState<Boolean>, navController: NavHostController, events: List<Event>) {
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
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                for (day in 0 until 7) {
                    val index = week * 7 + day
                    val dayText = calendarDays.getOrNull(index) ?: ""
                    val eventOnThisDay = events.find { it.date.split("/")[1] == dayText && it.date.split("/")[0] == currentMonth.monthValue.toString().padStart(2, '0') }
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .weight(1f)
                            .aspectRatio(1f)
                            .background(if (eventOnThisDay != null) Color(0xFFADD8E6) else Color.Transparent)
                            .clickable(enabled = dayText.isNotEmpty()) {
                                if (eventOnThisDay != null) {
                                    navController.navigate("event_details/${eventOnThisDay.date.replace("/", "-")}")
                                } else {
                                    showPopup.value = true
                                }
                            }
                    ) {
                        Text(
                            text = dayText,
                            modifier = Modifier.align(Alignment.Center),
                            color = if (dayText.isEmpty()) Color.Gray else Color.Black,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EventDetailsScreen(navController: NavHostController, date: String) {
    val events = listOf(
        Event("Calculus hw3", "Wednesday", "12/11/24", "6-9pm", "User1", "MA491"),
        Event("Physics Lab", "Thursday", "12/12/24", "2-4pm", "User2", "PH211"),
        Event("Group Study", "Friday", "12/13/24", "5-7pm", "User3", "CS501"),
        Event("Data Science Review", "Saturday", "12/14/24", "3-5pm", "User4", "DS201"),
        Event("Finals Review", "Tuesday", "12/17/24", "1-3pm", "User5", "CS501"),
        Event("Finals Review 2", "Tuesday", "12/17/24", "5-7pm", "User5", "CS501"),

    )
    val eventsOnThisDay = events.filter { it.date == date.replace("-", "/") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(text = "Events on $date", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(eventsOnThisDay) { event ->
                EventItem(event)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.popBackStack() }) {
            Text(text = "Back to Calendar")
        }
    }
}

@Composable
fun EventItem(event: Event) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFE0F7FA)) // Super light blue background
            .padding(8.dp)
    ) {
        Text(text = event.title, style = MaterialTheme.typography.titleMedium)
        Text(text = event.className, style = MaterialTheme.typography.bodyMedium)
        Text(text = "${event.day} ${event.date}", style = MaterialTheme.typography.bodySmall)
        Text(text = event.timeRange, style = MaterialTheme.typography.bodySmall)
        Text(text = event.username, style = MaterialTheme.typography.bodySmall)
    }
}