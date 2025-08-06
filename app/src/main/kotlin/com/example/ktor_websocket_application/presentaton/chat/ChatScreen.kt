package com.example.ktor_websocket_application.presentaton.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ktor_websocket_application.LocalSnackbarHostState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
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

@OptIn(FormatStringsInDatetimeFormats::class, ExperimentalLayoutApi::class)
@Composable
fun ChatScreen(
    username: String?,
    viewModel: ChatViewModel = hiltViewModel()
) {
    var shouldScrollEnd by rememberSaveable { mutableStateOf(value = true) }
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
            .imePadding()
    ) {

        val listState = rememberLazyListState()

        LaunchedEffect(shouldScrollEnd, listState.canScrollForward) {
            if (shouldScrollEnd && listState.canScrollForward) listState.animateToBottomFully()
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp, end = 16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(
                    space = 32.dp,
                    alignment = Alignment.Bottom
                ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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
                    .toSortedMap { date1, date2 -> -date1.compareTo(date2) }
                    .forEach { date, messages ->
                        stickyHeader {
                            Text(
                                text = date.format(
                                    format = LocalDate.Format {
                                        byUnicodePattern(pattern = "dd-MM-uuuu")
                                    }
                                ),
                                modifier = Modifier
                                    .background(
                                        color = Color.White,
                                        shape = RoundedCornerShape(size = 10.dp)
                                    )
                                    .padding(all = 8.dp),
                                fontWeight = FontWeight.Bold,
                                color = Color.Green
                            )
                        }
                        items(items = messages.sortedBy { it.time }) { (text, time, name, _) ->
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
                                        .widthIn(min = 60.dp)
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
                                        .padding(all = 8.dp),
                                    horizontalAlignment = if (isOwnMessage) Alignment.End else Alignment.Start
                                ) {
                                    Text(
                                        text = name,
                                        textAlign = if (isOwnMessage) TextAlign.End else TextAlign.Start,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = text,
                                        textAlign = if (isOwnMessage) TextAlign.End else TextAlign.Start,
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
                        item { }
                    }
            }
            val coroutineScope = rememberCoroutineScope()
            if (listState.canScrollForward) FilledIconButton(
                onClick = {
                    coroutineScope.launch { listState.animateToBottomFully() }
                },
                modifier = Modifier
                    .padding(end = 8.dp, bottom = 8.dp)
                    .size(48.dp),
                enabled = !listState.isScrollInProgress,
                shape = RoundedCornerShape(size = 10.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.White,
                    contentColor = Color.Green,
                    disabledContainerColor = Color.White.copy(alpha = 0.38f),
                    disabledContentColor = Color.Green.copy(alpha = 0.38f)
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Scroll end icon"
                )
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
                modifier = Modifier.weight(1f),
                trailingIcon = {
                    IconButton(onClick = viewModel::sendMessage) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send"

                        )
                    }
                },
                keyboardActions = KeyboardActions(onSend = { viewModel.sendMessage() }),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Send
                )
            )
        }
    }
}

private suspend fun LazyListState.animateToBottomFully() {
    val totalItems = layoutInfo.totalItemsCount
    if (totalItems == 0) return

    val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull { it.index == totalItems - 1 }

    if (lastVisibleItem != null) {
        val offsetNeeded = lastVisibleItem.size - lastVisibleItem.offset
        animateScrollToItem(index = totalItems - 1, scrollOffset = offsetNeeded)
    } else {
        animateScrollToItem(index = totalItems - 1)
    }
}
