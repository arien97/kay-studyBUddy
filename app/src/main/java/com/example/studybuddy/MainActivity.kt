package com.example.studybuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
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

    // Fetch groups when the app starts
    LaunchedEffect(Unit) {
        // Will replace after I figure out
        fetchGroups("RA5q6cTayGv3jAXnQ8HNsEgZrbBtnSYAVnuKhciK", groups)
    }

    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            NavHost(navController = navController, startDestination = "home") {
                composable("home") { HomeScreen() }
                composable("chats") { ChatsScreen(groups.value) }
                composable("profile") { ProfileScreen() }
            }
            NavigationBar(
                modifier = Modifier.align(Alignment.BottomCenter),
                containerColor = Color.White,
                contentColor = Color.Black
            ) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(imageVector = Icons.Default.Home, contentDescription = null) },
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

@Composable
fun ProfileScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "This is the Profile Screen")
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
