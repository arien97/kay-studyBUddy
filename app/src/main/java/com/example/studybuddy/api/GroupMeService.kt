package com.example.studybuddy.api

import retrofit2.http.GET
import retrofit2.http.Query

interface GroupMeService {
    @GET("groups")
    suspend fun getGroups(@Query("token") token: String): GroupResponse
}