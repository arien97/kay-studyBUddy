package com.example.studybuddy.domain

import kotlinx.serialization.Serializable


@Serializable
data class FriendListRow(
    var chatRoomUUID: String,
    var userEmail: String = "",
    var userUUID: String = "",
    var registerUUID: String = "",
    var userPictureUrl: String = "",
    var lastMessage: ChatMessage = ChatMessage()
)