@file:OptIn(ExperimentalMaterialApi::class)

package com.example.studybuddy.features.chats

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studybuddy.NavigationRoute
import com.example.studybuddy.domain.FriendListRegister
import com.example.studybuddy.domain.FriendListRow

@Composable
fun UserListRoute(
    chatAction: (NavigationRoute.Chat) -> Unit,
    userListViewModel: UserListViewModel = hiltViewModel(),
) {
    Box {
        LaunchedEffect(key1 = Unit) {
            userListViewModel.refreshingFriendList()
        }
        val acceptedFriendRequestList: MutableState<List<FriendListRow>> =
            userListViewModel.acceptedFriendRequestList
        val pendingFriendRequestList: MutableState<List<FriendListRegister>> =
            userListViewModel.pendingFriendRequestList


        val scrollState: LazyListState = rememberLazyListState()
        val refreshing: Boolean by userListViewModel.isRefreshing
        val state: PullRefreshState =
            rememberPullRefreshState(refreshing, { userListViewModel.refreshingFriendList() })
        val courses by userListViewModel.course.collectAsState()

        var showAlertDialog by remember {
            mutableStateOf(false)
        }
        if (showAlertDialog) {
            AlertDialogChat(
                onDismiss = { showAlertDialog = !showAlertDialog },
                onConfirm = {
                    showAlertDialog = !showAlertDialog
                    userListViewModel.createFriendshipRegisterToFirebase(it)
                })
        }

        List(
            acceptedFriendRequestList, pendingFriendRequestList,
            scrollState, state, { chatAction.invoke(it) },
            {
                userListViewModel.acceptPendingFriendRequestToFirebase(it.registerUUID)
                userListViewModel.refreshingFriendList()
            }, courses.toList()
        )

        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
            onClick = {
                showAlertDialog = !showAlertDialog
            },
            containerColor = MaterialTheme.colorScheme.primary,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp)
        ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = null)
        }
    }
}

@Composable
private fun List(
    acceptedFriendRequestList: MutableState<List<FriendListRow>>,
    pendingFriendRequestList: MutableState<List<FriendListRegister>>,
    scrollState: LazyListState,
    state: PullRefreshState,
    chatAction: (NavigationRoute.Chat) -> Unit,
    onAcceptClick: (FriendListRegister) -> Unit,
    courses: List<String>,
) {
    Column(
        modifier = Modifier
    ) {
        Text(
            text = "Open Chats",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = scrollState,
        ) {
            // Course Chats Section
            if (courses.isNotEmpty()) {
                item {
                    Text(
                        text = "Course Chats",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                items(courses) { course ->
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable {
                                val info = NavigationRoute.Chat.CourseChat(course)
                                chatAction.invoke(info)
                            }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(48.dp),
                            shape = CircleShape
                        ) {
                            Icon(
                                imageVector = Icons.Filled.School,
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(4.dp)
                                    .aspectRatio(1f)
                            )
                        }
                        Text(
                            text = course,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 16.dp)
                        )
                    }
                }
            }

            // Direct Messages Section
            item {
                Text(
                    text = "Direct Messages",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            if (acceptedFriendRequestList.value.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No direct messages yet",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Click + button to send a friend request",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            } else {
                items(acceptedFriendRequestList.value) { item ->
                    AcceptPendingRequestList(item) {
                        val info = NavigationRoute.Chat.PrivateChat(
                            item.chatRoomUUID,
                            item.registerUUID,
                            item.userUUID
                        )
                        chatAction.invoke(info)
                    }
                }
            }

            // Pending Requests Section
            if (pendingFriendRequestList.value.isNotEmpty()) {
                item {
                    Text(
                        text = "Pending Requests",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                items(pendingFriendRequestList.value) { item ->
                    PendingFriendRequestList(item) {
                        onAcceptClick.invoke(item)
                    }
                }
            }
        }
    }
}