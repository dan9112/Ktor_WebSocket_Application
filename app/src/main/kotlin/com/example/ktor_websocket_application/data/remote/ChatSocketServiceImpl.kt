package com.example.ktor_websocket_application.data.remote

import com.example.ktor_websocket_application.data.remote.serializers.MessageSerializer
import com.example.ktor_websocket_application.domain.model.Message
import com.example.ktor_websocket_application.util.Resource
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.url
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.serialization.json.Json

class ChatSocketServiceImpl(
    private val client: HttpClient
) : ChatSocketService {
    private var socket: WebSocketSession? = null

    override suspend fun initSession(username: String): Resource<Unit> {
        return try {
            socket = client.webSocketSession {
                url(urlString = "${ChatSocketService.Endpoints.ChatSocket.url}?username=$username")
            }
            if (socket?.isActive == true) {
                Resource.Success(data = Unit)
            } else {
                Resource.Error(message = "Couldn't establish a connection.")
            }
        } catch (e:Exception) {
            e.printStackTrace()
            Resource.Error(message = e.localizedMessage ?: "Unknown error")
        }
    }

    override suspend fun sendMessage(message: String) {
        try {
            socket?.send(frame = Frame.Text(text = message))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun observeMessages(): Flow<Message> {
        return try {
            socket
                ?.incoming
                ?.receiveAsFlow()
                ?.filterIsInstance(klass = Frame.Text::class)
                ?.map {
                    Json.decodeFromString(
                        deserializer = MessageSerializer,
                        string = it.readText()
                    )
                }
                ?: emptyFlow()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyFlow()
        }
    }

    override suspend fun closeSession() {
        socket?.close()
    }
}
