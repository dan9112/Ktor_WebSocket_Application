package com.example.ktor_websocket_application.domain.model

import kotlinx.datetime.LocalDateTime

interface Message {
    val text: String
    val time: LocalDateTime
    val userName: String
    val id: String
}
