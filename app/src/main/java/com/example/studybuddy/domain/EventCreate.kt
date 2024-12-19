package com.example.studybuddy.domain

data class EventCreate(
    val title: String? = null,
    val description: String? = null,
    val location: String? = null,
    val course: String? = null,
    val date: String? = null,
    val startTime: Float = 8f,
    val endTime: Float = 20f,
    val latitude: Double = 42.3505,
    val longitude: Double = -71.1054
)