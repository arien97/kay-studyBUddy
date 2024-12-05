package com.example.studybuddy.features.chats

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studybuddy.Constants
import com.example.studybuddy.data.UserListRepository
import com.example.studybuddy.domain.FriendListRegister
import com.example.studybuddy.domain.FriendListRow
import com.example.studybuddy.domain.FriendStatus
import com.example.studybuddy.domain.Response
import com.example.studybuddy.utils.ToastNotifier
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserListViewModel @Inject constructor(
    private val userListRepository: UserListRepository,
    private val toastNotifier: ToastNotifier,
) : ViewModel() {

    var pendingFriendRequestList = mutableStateOf<List<FriendListRegister>>(listOf())
        private set

    var acceptedFriendRequestList = mutableStateOf<List<FriendListRow>>(listOf())
        private set

    var isRefreshing = mutableStateOf(false)
        private set

    fun refreshingFriendList() {
        viewModelScope.launch {
            isRefreshing.value = true
            loadPendingFriendRequestListFromFirebase()
            loadAcceptFriendRequestListFromFirebase()
            delay(1000)
            isRefreshing.value = false
        }
    }

    fun createFriendshipRegisterToFirebase(acceptorEmail: String) {
        //Search User -> Check Chat Room -> Create Chat Room -> Check FriendListRegister -> Create FriendListRegister
        viewModelScope.launch {
            userListRepository.searchUserFromFirebase(acceptorEmail)
                .collect { response ->
                    when (response) {
                        is Response.Loading -> {
                        }

                        is Response.Success -> {
                            if (response.data != null) {
                                checkChatRoomExistFromFirebaseAndCreateIfNot(
                                    acceptorEmail, response.data.profileUUID,
                                )
                            } else {
                                toastNotifier.showMessage("Email not found")
                            }
                        }

                        is Response.Error -> {
                            toastNotifier.showMessage(response.message)
                        }
                    }

                }
        }
    }

    fun acceptPendingFriendRequestToFirebase(registerUUID: String) {
        viewModelScope.launch {
            userListRepository.acceptPendingFriendRequestToFirebase(registerUUID)
                .collect { response ->
                    when (response) {
                        is Response.Loading -> {
                        }

                        is Response.Success -> {
                            toastNotifier.showMessage("Friend Request Accepted")
                        }

                        is Response.Error -> {
                            toastNotifier.showMessage(response.message)
                        }
                    }
                }
        }
    }

    fun cancelPendingFriendRequestToFirebase(registerUUID: String) {
        viewModelScope.launch {
            userListRepository.rejectPendingFriendRequestToFirebase(registerUUID)
                .collect { response ->
                    when (response) {
                        is Response.Loading -> {

                        }

                        is Response.Success -> {
                            toastNotifier.showMessage("Friend Request Canceled")
                        }

                        is Response.Error -> {}
                    }
                }
        }
    }

    private fun loadAcceptFriendRequestListFromFirebase() {
        viewModelScope.launch {
            userListRepository.loadAcceptedFriendRequestListFromFirebase()
                .collect { response ->
                    when (response) {
                        is Response.Loading -> {}
                        is Response.Success -> {
                            if (response.data.isNotEmpty()) {
                                acceptedFriendRequestList.value = response.data
                            }
                        }

                        is Response.Error -> {}
                    }
                }
        }
    }

    private fun loadPendingFriendRequestListFromFirebase() {
        viewModelScope.launch {
            userListRepository.loadPendingFriendRequestListFromFirebase()
                .collect { response ->
                    when (response) {
                        is Response.Loading -> {}
                        is Response.Success -> {
                            pendingFriendRequestList.value = response.data
                        }

                        is Response.Error -> {}
                    }
                }
        }
    }

    private fun checkChatRoomExistFromFirebaseAndCreateIfNot(
        acceptorEmail: String,
        acceptorUUID: String,
    ) {
        viewModelScope.launch {
            userListRepository.checkChatRoomExistedFromFirebase(acceptorUUID)
                .collect { response ->
                    when (response) {
                        is Response.Loading -> {}
                        is Response.Success -> {
                            if (response.data == Constants.NO_CHATROOM_IN_FIREBASE_DATABASE) {
                                createChatRoomToFirebase(
                                    acceptorEmail,
                                    acceptorUUID,
                                )
                            } else {
                                checkFriendListRegisterIsExistFromFirebase(
                                    response.data,
                                    acceptorEmail,
                                    acceptorUUID,
                                )
                            }
                        }

                        is Response.Error -> {}
                    }
                }
        }
    }

    private fun createChatRoomToFirebase(
        acceptorEmail: String,
        acceptorUUID: String,
    ) {
        viewModelScope.launch {
            userListRepository.createChatRoomToFirebase(acceptorUUID)
                .collect { response ->
                    when (response) {
                        is Response.Loading -> {}
                        is Response.Success -> {
                            //Chat Room Created.
                            checkFriendListRegisterIsExistFromFirebase(
                                response.data,
                                acceptorEmail,
                                acceptorUUID,
                            )
                        }

                        is Response.Error -> {}
                    }
                }
        }
    }

    private fun checkFriendListRegisterIsExistFromFirebase(
        chatRoomUUID: String,
        acceptorEmail: String,
        acceptorUUID: String,
    ) {
        viewModelScope.launch {
            userListRepository.checkFriendListRegisterIsExistedFromFirebase(
                acceptorEmail,
                acceptorUUID
            ).collect { response ->
                when (response) {
                    is Response.Loading -> {
                    }

                    is Response.Success -> {
                        if (response.data.equals(FriendListRegister())) {
                            toastNotifier.showMessage("Friend Request Sent.")
                            createFriendListRegisterToFirebase(
                                chatRoomUUID,
                                acceptorEmail,
                                acceptorUUID,
                            )
                        } else if (response.data.status.equals(FriendStatus.PENDING.toString())) {
                            toastNotifier.showMessage("Already Have Friend Request")
                        } else if (response.data.status.equals(FriendStatus.ACCEPTED.toString())) {
                            toastNotifier.showMessage("You Are Already Friend.")
                        } else if (response.data.status.equals(FriendStatus.BLOCKED.toString())) {
                            openBlockedFriendToFirebase(response.data.registerUUID)
                        }
                    }

                    is Response.Error -> {}
                }
            }
        }
    }

    private fun createFriendListRegisterToFirebase(
        chatRoomUUID: String,
        acceptorEmail: String,
        acceptorUUID: String,
    ) {
        viewModelScope.launch {
            userListRepository.createFriendListRegisterToFirebase(
                chatRoomUUID,
                acceptorEmail,
                acceptorUUID,
            ).collect { response ->
                when (response) {
                    is Response.Loading -> {}
                    is Response.Success -> {
                    }

                    is Response.Error -> {}
                }

            }
        }
    }

    private fun openBlockedFriendToFirebase(registerUUID: String) {
        viewModelScope.launch {
            userListRepository.openBlockedFriendToFirebase(registerUUID)
                .collect { response ->
                    when (response) {
                        is Response.Loading -> {

                        }

                        is Response.Success -> {
                            if (response.data) {

                                toastNotifier.showMessage("User Block Opened And Accept As Friend")
                            } else {
                                toastNotifier.showMessage("You Are Blocked by User")
                            }

                        }

                        is Response.Error -> {}
                    }
                }
        }
    }
}