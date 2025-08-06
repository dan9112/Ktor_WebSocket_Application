package com.example.ktor_websocket_application.presentaton.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ktor_websocket_application.LocalSnackbarHostState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern

private data class MessageExceptDateInfo(
    val text: String,
    val time: LocalTime,
    val userName: String,
    val id: String
)

@OptIn(FormatStringsInDatetimeFormats::class)
@Composable
fun ChatScreen(
    username: String?,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val currentSnackbarHostState = LocalSnackbarHostState.current

    LaunchedEffect(key1 = true) {
        viewModel.toastEvent.collectLatest { message ->
            currentSnackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Long
            )
        }
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                viewModel.connectToChat()
            } else if (event == Lifecycle.Event.ON_STOP) {
                viewModel.disconnect()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 16.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            reverseLayout = true,
            verticalArrangement = Arrangement.spacedBy(space = 32.dp, alignment = Alignment.Bottom),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
            state
                .messages
                .groupBy(
                    keySelector = { it.time.date },
                    valueTransform = {
                        MessageExceptDateInfo(
                            text = it.text,
                            time = it.time.time,
                            userName = it.userName,
                            id = it.id
                        )
                    }
                )
                .toSortedMap()
                .forEach { date, messages ->
                    item {
                        Text(
                            text = date.format(
                                format = LocalDate.Format {
                                    byUnicodePattern(pattern = "dd-MM-uuuu")
                                }
                            ),
                            modifier = Modifier
                                .background(
                                    color = Color.Green,
                                    shape = RoundedCornerShape(size = 10.dp)
                                )
                                .padding(all = 8.dp),
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    items(items = messages) { (text, time, name, _) ->
                        val isOwnMessage = name == username
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = if (isOwnMessage) {
                                Alignment.CenterEnd
                            } else {
                                Alignment.CenterStart
                            }
                        ) {
                            Column(
                                modifier = Modifier
                                    .width(200.dp)
                                    .drawBehind {
                                        val cornerRadius = 10.dp.toPx()
                                        val triangleHeight = 20.dp.toPx()
                                        val triangleWidth = 25.dp.toPx()
                                        val trianglePath = Path().apply {
                                            if (isOwnMessage) {
                                                moveTo(
                                                    x = size.width,
                                                    y = size.height - cornerRadius
                                                )
                                                lineTo(
                                                    x = size.width,
                                                    y = size.height + triangleHeight
                                                )
                                                lineTo(
                                                    x = size.width - triangleWidth,
                                                    y = size.height - cornerRadius
                                                )
                                            } else {
                                                moveTo(x = 0f, y = size.height - cornerRadius)
                                                lineTo(x = 0f, y = size.height + triangleHeight)
                                                lineTo(
                                                    x = triangleWidth,
                                                    y = size.height - cornerRadius
                                                )
                                            }
                                            close()
                                        }
                                        drawPath(
                                            path = trianglePath,
                                            color = if (isOwnMessage) Color.Green else Color.DarkGray
                                        )
                                    }
                                    .background(
                                        color = if (isOwnMessage) Color.Green else Color.DarkGray,
                                        shape = RoundedCornerShape(size = 10.dp)
                                    )
                                    .padding(all = 8.dp)
                            ) {
                                Text(
                                    text = name,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = text,
                                    color = Color.White
                                )
                                Text(
                                    text = time.format(
                                        format = LocalTime.Format {
                                            byUnicodePattern(pattern = "HH:mm")
                                        }
                                    ),
                                    color = Color.White,
                                    modifier = Modifier.align(alignment = Alignment.End)
                                )
                            }
                        }
                    }
                }
        }
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            val message by viewModel.messageText.collectAsStateWithLifecycle()
            TextField(
                value = message,
                onValueChange = viewModel::onMessageChange,
                placeholder = {
                    Text(text = "Enter a message")
                },
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = viewModel::sendMessage) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send"

                )
            }
        }
    }
}
