package com.example.studybuddy.features.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studybuddy.MessageRegister
import com.example.studybuddy.data.AuthRepository
import com.example.studybuddy.data.ChatScreenRepository
import com.example.studybuddy.domain.Response
import com.example.studybuddy.domain.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatScreenViewModel @Inject constructor(
    private val chatScreenRepository: ChatScreenRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val currentUserId = authRepository.currentUserId
    var opponentProfileFromFirebase = mutableStateOf(User())
        private set

    var messageInserted = mutableStateOf(false)
        private set

    var messages: List<MessageRegister> by mutableStateOf(listOf())
        private set

    var messagesLoadedFirstTime = mutableStateOf(false)
        private set

    var toastMessage = mutableStateOf("")
        private set

    fun insertMessageToFirebase(
        chatRoomUUID: String,
        messageContent: String,
        registerUUID: String,
    ) {
        viewModelScope.launch {
            chatScreenRepository.insertMessageToFirebase(
                chatRoomUUID,
                messageContent,
                registerUUID,
            ).collect { response ->
                when (response) {
                    is Response.Loading -> {
                        messageInserted.value = false
                    }

                    is Response.Success -> {
                        messageInserted.value = true
                    }

                    is Response.Error -> {}
                }
            }
        }
    }

    fun loadMessagesFromFirebase(chatRoomUUID: String, opponentUUID: String, registerUUID: String) {
        viewModelScope.launch {
            chatScreenRepository.loadMessagesFromFirebase(chatRoomUUID, opponentUUID, registerUUID)
                .collect { response ->
                    when (response) {
                        is Response.Loading -> {}
                        is Response.Success -> {
                            messages = listOf()
                            for (i in response.data) {
                                if (i.profileUUID == opponentUUID) {
                                    messages =
                                        messages + MessageRegister(i, true) //Opponent Message
                                } else {
                                    messages = messages + MessageRegister(i, false) //User Message
                                }

                            }
                            messagesLoadedFirstTime.value = true
                        }

                        is Response.Error -> {}
                    }
                }
        }
    }

    fun loadCourseMessagesFromFirebase(course: String) {
        viewModelScope.launch {
            chatScreenRepository.loadCourseMessagesFromFirebase(course)
                .collect { response ->
                    when (response) {
                        is Response.Loading -> {}
                        is Response.Success -> {
                            messages = listOf()
                            for (i in response.data) {
                                if (i.profileUUID != currentUserId) {
                                    messages =
                                        messages + MessageRegister(i, true) //Opponent Message
                                } else {
                                    messages = messages + MessageRegister(i, false) //User Message
                                }

                            }
                            messagesLoadedFirstTime.value = true
                        }

                        is Response.Error -> {}
                    }
                }
        }
    }

    fun insertCourseMessageToFirebase(
        course: String, messageContent: String,
    ) {
        viewModelScope.launch {
            chatScreenRepository.insertCourseMessageToFirebase(course, messageContent)
                .collect { response ->
                    when (response) {
                        is Response.Loading -> {
                            messageInserted.value = false
                        }

                        is Response.Success -> {
                            messageInserted.value = true
                        }

                        is Response.Error -> {}
                    }
                }
        }
    }

    fun loadOpponentProfileFromFirebase(opponentUUID: String) {
        viewModelScope.launch {
            chatScreenRepository.loadOpponentProfileFromFirebase(opponentUUID).collect { response ->
                when (response) {
                    is Response.Loading -> {}
                    is Response.Success -> {
                        opponentProfileFromFirebase.value = response.data
                    }

                    is Response.Error -> {}
                }
            }
        }
    }

}
