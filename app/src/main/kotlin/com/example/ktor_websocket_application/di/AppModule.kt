package com.example.ktor_websocket_application.di

import com.example.ktor_websocket_application.data.remote.ChatSocketService
import com.example.ktor_websocket_application.data.remote.ChatSocketServiceImpl
import com.example.ktor_websocket_application.data.remote.MessageService
import com.example.ktor_websocket_application.data.remote.MessageServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.serialization.kotlinx.json.json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient {
        return HttpClient(engineFactory = CIO) {
            install(plugin = Logging)
            install(plugin = WebSockets)
            install(plugin = ContentNegotiation) {
                json()
            }
        }
    }

    @Provides
    @Singleton
    fun provideMessageService(client: HttpClient): MessageService = MessageServiceImpl(client = client)

    @Provides
    @Singleton
    fun provideChatSocketService(client: HttpClient): ChatSocketService = ChatSocketServiceImpl(client = client)
}
