package com.example.studybuddy

import com.example.studybuddy.domain.ChatMessage

data class MessageRegister(
    var chatMessage: ChatMessage,
    var isMessageFromOpponent: Boolean
)