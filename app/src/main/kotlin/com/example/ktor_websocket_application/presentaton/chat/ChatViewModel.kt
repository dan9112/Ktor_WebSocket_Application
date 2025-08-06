package com.example.ktor_websocket_application.presentaton.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ktor_websocket_application.data.remote.ChatSocketService
import com.example.ktor_websocket_application.data.remote.MessageService
import com.example.ktor_websocket_application.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val messageService: MessageService,
    private val chatSocketService: ChatSocketService,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _messageText = MutableStateFlow(value = "")
    val messageText = _messageText.asStateFlow()

    private val _state = MutableStateFlow(value = ChatState())
    val state = _state.asStateFlow()

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()

    fun connectToChat() {
        getAllMessages()
        savedStateHandle.get<String>("username")?.let { username ->
            viewModelScope.launch {
                val result = chatSocketService.initSession(username)
                when (result) {
                    is Resource.Success -> {
                        chatSocketService
                            .observeMessages()
                            .onEach { message ->
                                val newList = state.value.messages.toMutableList().apply {
                                    add(index = 0, element = message)
                                }
                                _state.value = ChatState(messages = newList)
                            }
                            .launchIn(scope = viewModelScope)
                    }

                    is Resource.Error -> {
                        _toastEvent.emit(value = result.message)
                    }
                }
            }
        }
    }

    fun onMessageChange(message: String) {
        _messageText.value = message
    }

    fun disconnect() {
        viewModelScope.launch {
            chatSocketService.closeSession()
        }
    }

    fun getAllMessages() {
        viewModelScope.launch {
            _state.value = state.value.copy(isLoading = true)
            _state.value = ChatState(messages = messageService.getAllMessages())
        }
    }

    fun sendMessage() {
        viewModelScope.launch {
            messageText.value.let { message ->
                if (message.isNotBlank()) chatSocketService.sendMessage(message = message)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        disconnect()
    }
}
