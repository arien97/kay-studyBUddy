package com.example.studybuddy

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import android.annotation.SuppressLint
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.graphics.PathSegment
import androidx.compose.material3.RangeSlider
import androidx.compose.ui.graphics.Color

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
    val items = listOf("home", "create_event", "chats", "profile")
    //val groups = remember { mutableStateOf<List<Group>>(emptyList()) }

    // Fetch groups when the app starts
    LaunchedEffect(Unit) {
        // Will replace after I figure out
        //fetchGroups("RA5q6cTayGv3jAXnQ8HNsEgZrbBtnSYAVnuKhciK", groups)
    }

    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            NavHost(navController = navController, startDestination = "home") {
                composable("home") { HomePage(navController) }
                composable("chats") { ChatsScreen() }
                composable("profile") { ProfileScreen() }
                composable("event_details/{eventId}") { backStackEntry ->
                    val eventId = backStackEntry.arguments?.getString("eventId")
                    EventDetailsPage(navController = navController, event = sampleEvent1)
                }
                composable("create_event") { CreateEventPage(navController) }
            }
            NavigationBar(
                modifier = Modifier.align(Alignment.BottomCenter),
                containerColor = Color.White,
                contentColor = Color.Black
            ) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(
                            imageVector = when (item) {
                                "home" -> Icons.Default.Home
                                "create_event" -> Icons.Default.Add
                                "chats" -> Icons.Default.Chat
                                "profile" -> Icons.Default.Person
                                else -> Icons.Default.Home
                            },
                            contentDescription = item.capitalize()) },
                        label = { Text(item.replace("_", " ").capitalize()) },
                        selected = selectedItem == index,
                        onClick = {
                            selectedItem = index
                            navController.navigate(item) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
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

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomePage(navController: NavController) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("create_event") },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Event")
            }
        }
    ) {
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
fun EventDetailsPage(navController: NavController, event: Event) {
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(54.dp),
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
                // Generic profile icon
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

                // Placeholder for Google Maps integration!!!!!!
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Map Placeholder (Google Maps API)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}



@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CreateEventPage(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Event") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) {
        Column(modifier = Modifier.padding(72.dp)) {
            OutlinedTextField(
                value = "",
                onValueChange = { /* Handle title input */ },
                label = { Text("Event Title") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = "",
                onValueChange = { /* Handle location input */ },
                label = { Text("Location") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = "",
                onValueChange = { /* Handle time input */ },
                label = { Text("Time") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = "",
                onValueChange = { /* Handle date input */ },
                label = { Text("Date") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = "",
                onValueChange = { /* Handle description input */ },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { /* Save event */ }, modifier = Modifier.align(Alignment.End)) {
                Text("Save Event")
            }
        }
    }
}



@Composable
fun ChatsScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "This is the Messages Screen")
    }
}

/*
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


@Composable
fun GroupList(groups: List<Group>) {
    LazyColumn {
        items(groups) { group ->
            Text(text = group.name)
        }
    }
}
 */

@Composable
fun ProfileScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "This is the Profile Screen")
    }
}

/*
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
*/

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
