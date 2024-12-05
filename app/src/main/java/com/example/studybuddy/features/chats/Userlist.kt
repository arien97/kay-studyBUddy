@file:OptIn(ExperimentalMaterialApi::class)

package com.example.studybuddy.features.chats

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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

        List(acceptedFriendRequestList, pendingFriendRequestList,
            scrollState, state, { chatAction.invoke(it) },
            {
                userListViewModel.acceptPendingFriendRequestToFirebase(it.registerUUID)
                userListViewModel.refreshingFriendList()
            }
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
) {

//    Box(Modifier.pullRefresh(state)) {

    Column(
        modifier = Modifier
    ) {
        Text(
            text = "Chats",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            state = scrollState,
        ) {
            items(acceptedFriendRequestList.value) { item ->
                AcceptPendingRequestList(item) {
                    val info = NavigationRoute.Chat(
                        item.chatRoomUUID,
                        item.registerUUID,
                        item.userUUID
                    )
                    chatAction.invoke(info)
                }
            }
            items(pendingFriendRequestList.value) { item ->
                PendingFriendRequestList(item, { onAcceptClick.invoke(item) })
            }
        }

//            PullRefreshIndicator(refreshing, state, Modifier.align(Alignment.TopCenter))

    }
//    }

}