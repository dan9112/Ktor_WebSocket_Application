package com.example.ktor_websocket_application.presentaton.user_name

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsernameViewModel @Inject constructor() : ViewModel() {
    private val _usernameText = MutableStateFlow(value = "")
    val usernameText = _usernameText.asStateFlow()

    private val _onJoinChat = MutableSharedFlow<String>()
    val onJoinChat = _onJoinChat.asSharedFlow()

    fun onUsernameChange(username: String) {
        _usernameText.value = username
    }

    fun onJoinClick() {
        viewModelScope.launch {
            if (usernameText.value.isNotBlank()) {
                _onJoinChat.emit(value = usernameText.value)
            }
        }
    }
}
