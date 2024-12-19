package com.example.studybuddy.features.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import com.example.studybuddy.components.EventPreviewCard
import com.example.studybuddy.domain.ProfilePicRepository
import com.example.studybuddy.domain.User

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
    val userEmail by viewModel.userEmail.collectAsState()
    val profilePictureUrl by viewModel.profilePictureUrl.collectAsState()
    val isEditing = remember { mutableStateOf(false) }
    val newProfilePictureUrl = remember { mutableStateOf(profilePictureUrl) }
    val showDialog = remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.uploadProfilePicture("userId", it, { newProfilePictureUrl.value = it }, { /* Handle failure */ }) }
    }

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
                    // Profile Picture and User Email
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ProfilePicture(imageUrl = profilePictureUrl)
                        IconButton(onClick = { launcher.launch("image/*") }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Profile Picture")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = userEmail, style = MaterialTheme.typography.titleLarge)
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
                                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
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
                color = MaterialTheme.colorScheme.primary,
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
fun ProfilePicture(
    imageUrl: String?
) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .background(MaterialTheme.colorScheme.primary, shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (imageUrl != null) {
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(50.dp)
            )
        }
    }
}