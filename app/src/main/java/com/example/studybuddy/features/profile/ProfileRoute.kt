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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    deleteAccount: () -> Unit,
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
        ProfileScreen(navController = rememberNavController(), signOut, deleteAccount, viewModel)
    }
}

@Composable
fun ProfileScreen(navController: NavHostController, signOut: () -> Unit, deleteAccount: () -> Unit, viewModel: ProfileViewModel) {
    val context = LocalContext.current
    val showAllEvents = remember { mutableStateOf(false) }
    val events = remember {
        mutableStateOf(
            listOf(
                Event("Calculus hw3", "Wednesday", "12/11/24", "6-9pm", "User1", "MA491"),
                Event("Physics Lab", "Thursday", "12/12/24", "2-4pm", "User2", "PH211"),
                Event("Group Study", "Friday", "12/13/24", "5-7pm", "User3", "CS501"),
                Event("Data Science Review", "Saturday", "12/14/24", "3-5pm", "User4", "DS201"),
                Event("Finals Review", "Tuesday", "12/17/24", "1-3pm", "User5", "CS501"),
                Event("Finals Review 2", "Tuesday", "12/17/24", "5-7pm", "User5", "CS501"),
            )
        )
    }
    val username by viewModel.username.collectAsState()
    val isEditing = remember { mutableStateOf(false) }
    val newUsername = remember { mutableStateOf(username) }
    val showDialog = remember { mutableStateOf(false) }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("Delete Account") },
            text = { Text("Are you sure you want to delete your account? This action cannot be undone.") },
            confirmButton = {
                Button(onClick = {
                    deleteAccount()
                    signOut()
                }) {
                    Text("Proceed")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog.value = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Profile",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(16.dp)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Log Out", style = MaterialTheme.typography.bodyMedium)
                        IconButton(onClick = { signOut() }) {
                            Icon(Icons.Default.ExitToApp, contentDescription = "Sign Out")
                        }
                    }
                }
            }
            item {
                // Profile Picture and Username
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CircleAvatar()
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        if (isEditing.value) {
                            TextField(
                                value = newUsername.value,
                                onValueChange = { newUsername.value = it },
                                label = { Text("Username") }
                            )
                            Button(onClick = {
                                viewModel.updateUsername(newUsername.value)
                                isEditing.value = false
                            }) {
                                Text("Save")
                            }
                        } else {
                            Text(text = username, style = MaterialTheme.typography.titleLarge)
                            IconButton(onClick = { isEditing.value = true }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Profile")
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
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

            item {
                Spacer(modifier = Modifier.height(32.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Delete Account",
                        color = Color.Red,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { showDialog.value = true }
                    )
                }
            }
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
                Text(text = event.className, style = MaterialTheme.typography.bodyMedium)
                Text(text = "${event.day} ${event.timeRange}")
                Text(text = event.username)
            }
        }
    }
}

data class Event(
    val title: String,
    val day: String,
    val date: String,
    val timeRange: String,
    val username: String,
    val className: String
)