package com.example.ktor_websocket_application.presentaton.chat

import com.example.ktor_websocket_application.domain.model.Message

data class ChatState(
    val messages: List<Message> = emptyList(),
    val isLoading: Boolean = false
)
