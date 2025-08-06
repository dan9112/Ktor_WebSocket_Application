package com.example.ktor_websocket_application.presentaton.user_name

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ktor_websocket_application.presentaton.Screen
import kotlinx.coroutines.flow.collectLatest

@Composable
fun UsernameScreen(
    viewModel: UsernameViewModel = hiltViewModel(),
    onAuth: (String) -> Unit
) {
    val usernameText = viewModel.usernameText.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = true) {
        viewModel.onJoinChat.collectLatest { username ->
            onAuth(username)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.End
        ) {
            TextField(
                value = usernameText.value,
                onValueChange = viewModel::onUsernameChange,
                placeholder = {
                    Text(text = "Enter a username...")
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(height = 8.dp))
            Button(onClick = viewModel::onJoinClick) {
                Text(text = "Join")
            }
        }
    }
}
