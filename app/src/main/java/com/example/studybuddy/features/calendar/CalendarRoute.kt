package com.example.studybuddy.features.calendar

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.snapping.SnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle


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
        Event("Finals Review", "Tuesday", "12/17/24", "1-3pm", "User5", "CS501"),
        Event("Finals Review 2", "Tuesday", "12/17/24", "5-7pm", "User5", "CS501")
    )

    val currentMonth = remember { mutableStateOf(YearMonth.now()) }
    val pagerState = rememberPagerState(initialPage = 1, pageCount = { 12 })
    val displayedMonth by remember {
        derivedStateOf {
            currentMonth.value.plusMonths((pagerState.currentPage - 1).toLong())
        }
    }

    val currentDate = LocalDate.now()
    val currentDayText = "${currentDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())}, ${currentDate.format(DateTimeFormatter.ofPattern("MMMM dd"))}"

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Text(
            text = "Calendar",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically, // Aligns children along the vertical center
            modifier = Modifier.fillMaxWidth()
        ) {
            // Display current day
            Text(
                text = currentDayText,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            // "Today" Button that jumps to current month
            Button(
                onClick = {
                    coroutineScope.launch {
                        val todayPage = 1 // Assuming the current month is at page index 1
                        pagerState.scrollToPage(todayPage)
                    }
                }
            ) {
                Text(text = "Today")
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        // Month Navigation and Today Button
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = {
                coroutineScope.launch {
                    pagerState.scrollToPage(pagerState.currentPage - 1)
                }
            }) {
                Icon(imageVector = Icons.Default.KeyboardArrowUp, contentDescription = "Previous Month")
            }
            Text(
                text = displayedMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                style = MaterialTheme.typography.titleLarge
            )
            IconButton(onClick = {
                coroutineScope.launch {
                    pagerState.scrollToPage(pagerState.currentPage + 1)
                }
            }) {
                Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = "Next Month")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val screenHeightDp = LocalConfiguration.current.screenHeightDp.dp
        val pageHeight = (screenHeightDp / 2.5f)

        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            pageSize = PageSize.Fixed(pageHeight)
        ) { page ->
            val month = currentMonth.value.plusMonths((page - 1).toLong())

            // Check if this month is the same as the displayed month
            val isCurrentMonth = month == displayedMonth

            // Set modifier to grey out months that aren't the displayed month
            val monthModifier = if (isCurrentMonth) {
                Modifier.fillMaxSize()
            } else {
                Modifier.fillMaxSize().graphicsLayer { alpha = 0.3f }
            }

            // Display the month's content
            MonthView(
                modifier = monthModifier,
                month = month,
                currentMonth = currentMonth.value,
                navController = navController,
                events = events,
                showPopup = showPopup,
                currentDate = currentDate
            )
        }
    }

    if (showPopup.value) {
        AlertDialog(
            onDismissRequest = { showPopup.value = false },
            title = { Text("Events") },
            text = { Text("You have no events on this day, go to Home to create an event.") },
            confirmButton = {
                Button(onClick = { showPopup.value = false }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun MonthView(
    modifier: Modifier = Modifier,
    month: YearMonth,
    currentMonth: YearMonth,
    navController: NavHostController,
    events: List<Event>,
    showPopup: MutableState<Boolean>,
    currentDate: LocalDate
) {
    val firstDayOfMonth = month.atDay(1)
    val dayOfWeek = firstDayOfMonth.dayOfWeek.value % 7 // Adjust to make Sunday = 0
    val daysInMonth = month.lengthOfMonth()
    val days = (1..daysInMonth).toList()
    val calendarDays = MutableList(42) { "" }

    // Populate the calendar days
    for (i in days.indices) {
        calendarDays[dayOfWeek + i] = days[i].toString()
    }

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        // Month title
        Text(
            text = month.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(8.dp)
        )

        // Days of the Week Header
        DaysOfWeekHeader()

        // Calendar grid
        for (week in 0 until 6) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (day in 0 until 7) {
                    val index = week * 7 + day
                    val dayText = calendarDays.getOrNull(index) ?: ""
                    val isCurrentMonth = dayText.isNotEmpty()
                    val eventOnThisDay = events.find {
                        it.date.split("/")[1] == dayText &&
                                it.date.split("/")[0] == month.monthValue.toString().padStart(2, '0')
                    }
                    val isToday = isCurrentMonth && currentDate.dayOfMonth.toString() == dayText &&
                            currentDate.monthValue == month.monthValue && currentDate.year == month.year

                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .weight(1f)
                            .aspectRatio(1f)
                            .background(if (eventOnThisDay != null) Color(0xFFADD8E6) else Color.Transparent)
                            .clickable(enabled = isCurrentMonth) {
                                if (eventOnThisDay != null) {
                                    navController.navigate("event_details/${eventOnThisDay.date.replace("/", "-")}")
                                } else {
                                    showPopup.value = true
                                }
                            }
                    ) {
                        Text(
                            text = dayText,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .background(
                                    color = if (isToday) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    shape = CircleShape
                                )
                                .padding(8.dp),
                            color = when {
                                isToday -> Color.White // Current day text color is white
                                isCurrentMonth -> Color.Black // Default color for current month's days
                                else -> Color.Gray // Default color for other months' days
                            },
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DaysOfWeekHeader() {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        for (day in daysOfWeek) {
            Text(
                text = day,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
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
        Event("Finals Review 2", "Tuesday", "12/17/24", "5-7pm", "User5", "CS501")
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