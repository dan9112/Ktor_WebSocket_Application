package com.example.ktor_websocket_application.presentaton

import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen {
    @Serializable
    data object Username: Screen
    @Serializable
    data class Chat(val username: String): Screen
}
