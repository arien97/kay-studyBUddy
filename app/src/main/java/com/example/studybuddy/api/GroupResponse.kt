package com.example.studybuddy.api

import com.squareup.moshi.Json

data class GroupResponse(
    @Json(name = "response") val response: List<Group>
)

data class Group(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "description") val description: String?
)