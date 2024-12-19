package com.example.studybuddy.features.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.studybuddy.NavigationRoute
import com.example.studybuddy.features.calendar.CalendarRoute
import com.example.studybuddy.features.chats.UserListRoute
import com.example.studybuddy.features.discovery.DiscoveryRoute
import com.example.studybuddy.features.discovery.EventDetailsPage
import com.example.studybuddy.features.discovery.CreateEventRoute
import com.example.studybuddy.features.profile.ProfileRoute
import kotlinx.serialization.Serializable

@Composable
fun MainTabsRoute(
    signOut: () -> Unit,
    deleteAccount: () -> Unit,
    changeCourse: () -> Unit,
    chatAction: (NavigationRoute.Chat) -> Unit,
) {
    val navController = rememberNavController()
    val tabs = listOf(Tab.Home, Tab.ChatList, Tab.CreateEvent, Tab.Calendar, Tab.Profile)
    var selectedItem by rememberSaveable { mutableIntStateOf(0) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
    ) {
        NavHost(
            navController = navController,
            startDestination = Tab.Home,
            modifier = Modifier.weight(1f)
        ) {
            composable<Tab.Home> { DiscoveryRoute(navController) }
            composable<Tab.ChatList> { UserListRoute({ chatAction.invoke(it) }) }
            composable<Tab.CreateEvent> { CreateEventRoute(navController) }
            composable<Tab.Profile> {
                ProfileRoute(
                    signOut = signOut,
                    changeCourse = changeCourse,
                    deleteAccount = deleteAccount,
                    onNavigateToEventDetails = { eventId ->
                        navController.navigate("event_details/$eventId")
                    }
                )
            }
            composable<Tab.Calendar> { CalendarRoute(navController) }

            composable("event_details/{eventId}") { backStackEntry ->
                val eventId = backStackEntry.arguments?.getString("eventId")
                EventDetailsPage(navController, eventId ?: return@composable)
            }
        }
        NavigationBar(
            modifier = Modifier.fillMaxWidth(),
            containerColor = Color.White,
            contentColor = Color.Black
        ) {
            tabs.forEachIndexed { index, item ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = when (item) {
                                Tab.Home -> Icons.Default.Home
                                Tab.ChatList -> Icons.Default.Chat
                                Tab.Profile -> Icons.Default.Person
                                Tab.CreateEvent -> Icons.Default.Add
                                Tab.Calendar -> Icons.Default.CalendarMonth
                            },
                            contentDescription = null
                        )
                    },
                    label = { Text(item.name) },
                    selected = selectedItem == index,
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.primary,
                        selectedIconColor = MaterialTheme.colorScheme.tertiary,
                    ),
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

@Serializable
sealed class Tab(val name: String) {
    @Serializable
    data object Home : Tab("Home")

    @Serializable
    data object ChatList : Tab("Chats")

    @Serializable
    data object CreateEvent : Tab("Create Event")

    @Serializable
    data object Profile : Tab("Profile")

    @Serializable
    data object Calendar : Tab("Calendar")
}