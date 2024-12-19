package com.example.studybuddy.domain

import java.util.UUID

data class Event(
    val id: String = UUID.randomUUID().toString(),

    val title: String? = null,
    val description: String? = null,
    val course: String? = null,

    val date: String? = null,
    val startTime: Float? = null,
    val endTime: Float? = null,

    val authorUsername: String? = null,
    val authorUUID: String? = null,

    val location: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)