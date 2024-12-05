package com.example.studybuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.studybuddy.api.Group
import com.example.studybuddy.api.GroupMeApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    var selectedItem by remember { mutableStateOf(0) }
    val items = listOf("home", "chats", "profile")
    val groups = remember { mutableStateOf<List<Group>>(emptyList()) }
    val events = remember { mutableStateOf(listOf(
        Event("Calculus hw3", "Wednesday", "6-9pm", "User1"),
        Event("Physics Lab", "Thursday", "2-4pm", "User2"),
        Event("Group Study", "Friday", "5-7pm", "User3"),
        Event("Chemistry Review", "Saturday", "3-5pm", "User4")
    )) }

    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            NavHost(navController = navController, startDestination = "home") {
                composable("home") { HomeScreen() }
                composable("chats") { ChatsScreen(groups.value) }
                composable("profile") { ProfileScreen(navController) }
                composable("settings") { SettingsScreen(navController) }
            }
            NavigationBar(
                modifier = Modifier.align(Alignment.BottomCenter),
                containerColor = Color.White,
                contentColor = Color.Black
            ) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = {
                            when (item) {
                                "chats" -> Icon(imageVector = Icons.Default.Notifications, contentDescription = null)
                                "profile" -> Icon(imageVector = Icons.Default.Person, contentDescription = null)
                                else -> Icon(imageVector = Icons.Default.Home, contentDescription = null)
                            }
                        },
                        label = { Text(item.capitalize()) },
                        selected = selectedItem == index,
                        onClick = {
                            selectedItem = index
                            navController.navigate(item) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun HomeScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "This is the Home Screen")
    }
}

@Composable
fun GroupList(groups: List<Group>) {
    LazyColumn {
        items(groups) { group ->
            Text(text = group.name)
        }
    }
}

@Composable
fun ChatsScreen(groups: List<Group>) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (groups.isEmpty()) {
            Text(text = "This is the Messages Screen")
        } else {
            //GroupList(groups)
            Text(text = "We have connected to GroupMe!")
        }
    }
}

fun fetchGroups(token: String, groupsState: MutableState<List<Group>>) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = GroupMeApi.service.getGroups(token)
            // Update the state with the fetched groups
            withContext(Dispatchers.Main) {
                groupsState.value = response.response
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
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

//            item {
//                // Placeholder for latest chat
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(100.dp)
//                        .background(Color(0xFF1E90FF)) // Specific blue color
//                ) {
//                    Text(text = "Latest Chat", modifier = Modifier.align(Alignment.Center), color = Color.White)
//                }
//                Spacer(modifier = Modifier.height(16.dp))
//            }

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
        IconButton(
            onClick = { navController.navigate("settings") },
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings", tint = Color(0xFF1E90FF)) // Specific blue color
        }

        // Popup Message
        if (showPopup.value) {
            AlertDialog(
                onDismissRequest = { showPopup.value = false },
                title = { Text(text = "Day Clicked") },
                text = { Text("You clicked on a day!") },
                confirmButton = {
                    Button(onClick = { showPopup.value = false }) {
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
    val daysInMonth = 31
    val firstDayOfWeek = 0 // 0 for Sunday, 1 for Monday, etc.
    val days = (1..daysInMonth).toList()
    val calendarDays = MutableList(35) { "" }

    for (i in days.indices) {
        calendarDays[firstDayOfWeek + i] = days[i].toString()
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "December", style = MaterialTheme.typography.titleMedium)
        for (week in 0 until 5) {
            Row {
                for (day in 0 until 7) {
                    val index = week * 7 + day
                    val dayText = calendarDays.getOrNull(index) ?: ""
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .size(40.dp) // Adjusted size to fit all dates
                            .background(if (dayText.isEmpty()) Color(0xFFADD8E6) else Color.White) // Light blue color for empty days
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
fun SettingsScreen(navController: NavHostController) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Button(onClick = { /* Change Username */ }, colors = ButtonDefaults.buttonColors(Color(0xFF1E90FF))) {
            Text("Change Username")
        }
        Button(onClick = { /* Delete Account */ }, colors = ButtonDefaults.buttonColors(Color(0xFF1E90FF))) {
            Text("Delete Account")
        }
        Button(onClick = { navController.popBackStack() }, colors = ButtonDefaults.buttonColors(Color(0xFF1E90FF))) {
            Text("Back")
        }
    }
}

@Composable
fun EventItem(event: Event) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(text = event.title, style = MaterialTheme.typography.titleMedium)
        Text(text = "${event.day} ${event.timeRange}")
        Text(text = event.username)
    }
}

data class Event(
    val title: String,
    val day: String,
    val timeRange: String,
    val username: String
)