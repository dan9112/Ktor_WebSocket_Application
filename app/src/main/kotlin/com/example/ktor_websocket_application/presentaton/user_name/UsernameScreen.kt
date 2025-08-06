package com.example.ktor_websocket_application.presentaton.user_name

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
            .padding(all = 16.dp)
            .imePadding(),
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
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = viewModel::onJoinClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Enter"
                        )
                    }
                },
                keyboardActions = KeyboardActions(onDone = { viewModel.onJoinClick() }),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                singleLine = true
            )
        }
    }
}
