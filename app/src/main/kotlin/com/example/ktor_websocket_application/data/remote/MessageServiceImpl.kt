package com.example.ktor_websocket_application.data.remote

import com.example.ktor_websocket_application.data.remote.serializers.MessageSerializer
import com.example.ktor_websocket_application.domain.model.Message
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

class MessageServiceImpl(
    private val client: HttpClient
) : MessageService {
    override suspend fun getAllMessages(): List<Message> {
        return try {
            client.get(urlString = MessageService.Endpoints.GetAllMessages.url)
                .body<List<JsonObject>>()
                .map { Json.decodeFromJsonElement(deserializer = MessageSerializer, element = it) }
        } catch (e: Exception) {
            emptyList()// todo: add error handling!
        }
    }
}
