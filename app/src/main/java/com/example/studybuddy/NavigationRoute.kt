package com.example.studybuddy

import kotlinx.serialization.Serializable

@Serializable
sealed interface NavigationRoute {

    @Serializable
    data object Loading : NavigationRoute

    @Serializable
    data object SignIn : NavigationRoute

    @Serializable
    data object SignUp : NavigationRoute

    @Serializable
    data object EmailLink : NavigationRoute

    @Serializable
    data object Onboarding : NavigationRoute

    @Serializable
    data object Tabs : NavigationRoute


    @Serializable
    data class Chat(
        val chatRoomUUID: String,
        val registerUUID: String,
        val opponentUUID: String,
    ) : NavigationRoute

}