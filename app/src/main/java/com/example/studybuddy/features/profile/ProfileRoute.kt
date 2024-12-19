package com.example.studybuddy.features.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.studybuddy.components.EventPreviewCard
import com.example.studybuddy.domain.Event

@Composable
fun ProfileRoute(
    signOut: () -> Unit,
    changeCourse: () -> Unit,
    deleteAccount: () -> Unit,
    onNavigateToEventDetails: (String) -> Unit, // Add this instead of NavController
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
        ProfileScreen(
            signOut = signOut,
            changeCourse = changeCourse,
            deleteAccount = deleteAccount,
            onNavigateToEventDetails = onNavigateToEventDetails,
            viewModel = viewModel
        )
    }
}

@Composable
fun ProfileScreen(
    signOut: () -> Unit,
    changeCourse: () -> Unit,
    deleteAccount: () -> Unit,
    onNavigateToEventDetails: (String) -> Unit,
    viewModel: ProfileViewModel
) {
    val context = LocalContext.current
    val showAllEvents = remember { mutableStateOf(false) }

    val events by viewModel.events.collectAsState()
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
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
                if (events.isEmpty()) {
                    item {
                        Text(
                            text = "No events created yet. Start by creating a new event!",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    val eventsToShow = if (showAllEvents.value) events else events.take(3)
                    items(eventsToShow) { event ->
                        EventPreviewCard(
                            event = event,
                            onClick = {
                                event.id?.let { eventId ->
                                    onNavigateToEventDetails(eventId)
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }


                    if (events.size > 3) {
                        item {
                            Button(
                                onClick = { showAllEvents.value = !showAllEvents.value },
                                colors = ButtonDefaults.buttonColors(Color(0xFF1E90FF))
                            ) {
                                Text(
                                    if (showAllEvents.value) "Show Less" else "Show More",
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }


            Text(
                text = "Change course",
                color = Color.Blue,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable { changeCourse.invoke() }
                    .padding(16.dp)
            )

            Text(
                text = "Delete Account",
                color = Color.Red,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable { showDialog.value = true }
                    .padding(16.dp)
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
                Text(text = event.title.orEmpty(), style = MaterialTheme.typography.titleMedium)
                Text(text = event.course.orEmpty(), style = MaterialTheme.typography.bodyMedium)
                Text(text = event.date.orEmpty())
                Text(text = event.authorUsername.orEmpty())
            }
        }
    }
}
