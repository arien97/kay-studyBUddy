package com.example.studybuddy.domain

data class FriendListRegister(
    var chatRoomUUID: String = "",
    var registerUUID: String = "",
    var requesterEmail: String = "",
    var requesterUUID: String = "",
    var requesterOneSignalUserId: String = "",
    var acceptorEmail: String = "",
    var acceptorUUID: String = "",
    var status: String = "",
    var lastMessage: ChatMessage = ChatMessage()
)