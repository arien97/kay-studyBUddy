package com.example.studybuddy.domain

import kotlinx.serialization.Serializable

@Serializable
data class ChatMessage(
    val profileUUID: String = "",
    val message: String = "",
    val date: Long = 0,
    val status: String = ""
)