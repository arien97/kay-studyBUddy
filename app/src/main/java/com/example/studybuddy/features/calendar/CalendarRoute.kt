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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.VerticalPager
import com.google.accompanist.pager.rememberPagerState
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

@OptIn(ExperimentalPagerApi::class)
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
    val pagerState = rememberPagerState(initialPage = 1)
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
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Calendar",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Display current day
        Text(
            text = currentDayText,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Month Navigation
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

        Spacer(modifier = Modifier.height(16.dp))

        // Continuous Calendar Scroll
        VerticalPager(
            state = pagerState,
            count = 12,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val month = currentMonth.value.plusMonths((page - 1).toLong())
            MonthView(
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

    for (i in days.indices) {
        calendarDays[dayOfWeek + i] = days[i].toString()
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = month.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(8.dp)
        )

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
                            color = if (isCurrentMonth) Color.Black else Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CalendarView(
    showPopup: MutableState<Boolean>,
    navController: NavHostController,
    events: List<Event>,
    lazyListState: LazyListState,
    scrollToIndex: Int
) {
    val currentMonth = remember { mutableStateOf(YearMonth.now()) }
    val monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.fillMaxSize()
    ) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            state = lazyListState // Attach lazyListState for scrolling
        ) {
            items(listOf(currentMonth.value.minusMonths(1), currentMonth.value, currentMonth.value.plusMonths(1))) { month ->
                Text(
                    text = month.format(monthFormatter), // Format the `YearMonth` instance
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (month == currentMonth.value) MaterialTheme.colorScheme.primary else Color.Gray
                    ),
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .clickable {
                            currentMonth.value = month // Update the displayed month on click
                        }
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        DaysOfWeekHeader()
        CalendarGrid(currentMonth.value, showPopup, navController, events)
    }

    // Use LaunchedEffect to call the suspend function
    LaunchedEffect(key1 = scrollToIndex) {
        lazyListState.animateScrollToItem(scrollToIndex)
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
fun CalendarGrid(
    currentMonth: YearMonth,
    showPopup: MutableState<Boolean>,
    navController: NavHostController,
    events: List<Event>
) {
    val firstDayOfMonth = currentMonth.atDay(1)
    val dayOfWeek = firstDayOfMonth.dayOfWeek.value % 7 // Adjust to make Sunday = 0
    val daysInMonth = currentMonth.lengthOfMonth()
    val days = (1..daysInMonth).toList()
    val calendarDays = MutableList(42) { "" }

    // Populate calendar days with appropriate dates
    for (i in days.indices) {
        calendarDays[dayOfWeek + i] = days[i].toString()
    }

    // Calendar Grid
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        for (week in 0 until 6) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (day in 0 until 7) {
                    val index = week * 7 + day
                    val dayText = calendarDays.getOrNull(index) ?: ""
                    val eventOnThisDay = events.find {
                        it.date.split("/")[1] == dayText &&
                                it.date.split("/")[0] == currentMonth.monthValue.toString().padStart(2, '0')
                    }

                    // Display each day
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