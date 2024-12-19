package com.example.studybuddy.features.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.studybuddy.components.EventPreviewCard
import com.example.studybuddy.domain.Event
import com.example.studybuddy.utils.DateUtils
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarRoute(
    navController: NavHostController = rememberNavController(),
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val events: List<Event> by viewModel.events.collectAsState()
    CalendarScreen(navController, events)
}

@Composable
fun CalendarScreen(navController: NavHostController, events: List<Event>) {
    val showPopup = remember { mutableStateOf(false) }
    val currentMonth = remember { mutableStateOf(YearMonth.now()) }
    val pagerState = rememberPagerState(initialPage = 1, pageCount = { 12 })
    val displayedMonth by remember {
        derivedStateOf {
            currentMonth.value.plusMonths((pagerState.currentPage - 1).toLong())
        }
    }

    val currentDate = LocalDate.now()
    val currentDayText = "${currentDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())}, " +
            "${currentDate.format(DateTimeFormatter.ofPattern("MMMM dd"))}"

    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Calendar",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = currentDayText,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Button(
                    onClick = {
                        coroutineScope.launch {
                            pagerState.scrollToPage(1)
                        }
                    }
                ) {
                    Text(text = "Today")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val screenHeightDp = LocalConfiguration.current.screenHeightDp.dp
            val pageHeight = (screenHeightDp / 2.5f)

            VerticalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                pageSize = PageSize.Fixed(pageHeight)
            ) { page ->
                val month = currentMonth.value.plusMonths((page - 1).toLong())
                val isCurrentMonth = month == displayedMonth
                val monthModifier = if (isCurrentMonth) {
                    Modifier.fillMaxSize()
                } else {
                    Modifier.fillMaxSize().graphicsLayer { alpha = 0.3f }
                }

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

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
                .background(MaterialTheme.colorScheme.primary)
                .padding(4.dp)
        ) {
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        pagerState.scrollToPage(pagerState.currentPage - 1)
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Previous Month",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        pagerState.scrollToPage(pagerState.currentPage + 1)
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Next Month",
                    tint = Color.White
                )
            }
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
    val dayOfWeek = firstDayOfMonth.dayOfWeek.value % 7
    val daysInMonth = month.lengthOfMonth()
    val days = (1..daysInMonth).toList()
    val calendarDays = MutableList(42) { "" }
    var selectedDayEvents by remember { mutableStateOf<List<Event>?>(null) }

    for (i in days.indices) {
        calendarDays[dayOfWeek + i] = days[i].toString()
    }

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = month.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(8.dp)
        )

        DaysOfWeekHeader()

        for (week in 0 until 6) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (day in 0 until 7) {
                    val index = week * 7 + day
                    val dayText = calendarDays.getOrNull(index) ?: ""
                    val isCurrentMonth = dayText.isNotEmpty()

                    val eventsOnThisDay = events.filter {
                        val eventDate = it.date?.let(DateUtils::stringToDate) ?: return@filter false
                        eventDate.dayOfMonth.toString() == dayText &&
                                eventDate.monthValue == month.monthValue &&
                                eventDate.year == month.year
                    }

                    val isToday = isCurrentMonth && currentDate.dayOfMonth.toString() == dayText &&
                            currentDate.monthValue == month.monthValue &&
                            currentDate.year == month.year

                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .weight(1f)
                            .aspectRatio(1f)
                            .background(if (eventsOnThisDay.isNotEmpty()) MaterialTheme.colorScheme.surface else Color.Transparent)
                            .clickable(enabled = isCurrentMonth) {
                                if (eventsOnThisDay.isNotEmpty()) {
                                    selectedDayEvents = eventsOnThisDay
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
    selectedDayEvents?.let { events ->
        DayEventsDialog(
            events = events,
            onEventClick = { event ->
                navController.navigate("event_details/${event.id}")
                selectedDayEvents = null  // Close dialog after navigation
            },
            onDismiss = { selectedDayEvents = null }
        )
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
fun DayEventsDialog(
    events: List<Event>,
    onEventClick: (Event) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Events") },
        text = {
            LazyColumn {
                items(events) { event ->
                    EventPreviewCard(
                        event = event,
                        onClick = { onEventClick(event) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
