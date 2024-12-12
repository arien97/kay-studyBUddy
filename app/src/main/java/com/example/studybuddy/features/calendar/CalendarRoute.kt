package com.example.studybuddy.features.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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

@Composable
fun CalendarRoute(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = "calendar") {
        composable("calendar") {
            CalendarScreen(navController)
        }
        composable("placeholder") {
            PlaceholderScreen(navController)
        }
    }
}

@Composable
fun CalendarScreen(navController: NavHostController) {
    val showPopup = remember { mutableStateOf(false) }
    CalendarView(showPopup, navController)

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
fun CalendarView(showPopup: MutableState<Boolean>, navController: NavHostController) {
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
        CalendarGrid(currentMonth.value, showPopup, navController)
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
fun CalendarGrid(currentMonth: YearMonth, showPopup: MutableState<Boolean>, navController: NavHostController) {
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
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .weight(1f)
                            .aspectRatio(1f)
                            .background(if (dayText == "15") Color(0xFFADD8E6) else Color.White)
                            .clickable(enabled = dayText.isNotEmpty()) {
                                if (dayText == "15") {
                                    navController.navigate("placeholder")
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
fun PlaceholderScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "This is a placeholder screen.")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.popBackStack() }) {
            Text(text = "Back to Calendar")
        }
    }
}