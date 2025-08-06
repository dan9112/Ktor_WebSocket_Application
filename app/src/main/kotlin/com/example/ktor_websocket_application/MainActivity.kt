package com.example.ktor_websocket_application

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.ktor_websocket_application.presentaton.Screen
import com.example.ktor_websocket_application.presentaton.chat.ChatScreen
import com.example.ktor_websocket_application.presentaton.user_name.UsernameScreen
import com.example.ktor_websocket_application.ui.theme.KtorWebSocketApplicationTheme
import dagger.hilt.android.AndroidEntryPoint

val LocalSnackbarHostState = compositionLocalOf<SnackbarHostState> {
    error("No Snackbar Host State")
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalLayoutApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val snackbarHostState = remember { SnackbarHostState() }
            KtorWebSocketApplicationTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = "Background image",
                        modifier = Modifier
                            .size(size = 45.dp)
                            .align(alignment = Alignment.Center),
                        contentScale = ContentScale.Crop,
                        alpha = 0.35f
                    )
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        snackbarHost = {
                            SnackbarHost(
                                hostState = snackbarHostState,
                                modifier = Modifier.imePadding()
                            )
                        }
                    ) { innerPadding ->
                        val navController = rememberNavController()
                        CompositionLocalProvider(value = LocalSnackbarHostState provides snackbarHostState) {
                            NavHost(
                                navController = navController,
                                startDestination = Screen.Username,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(paddingValues = innerPadding)
                                    .consumeWindowInsets(innerPadding)
                            ) {
                                composable<Screen.Username> {
                                    UsernameScreen(
                                        onAuth = {
                                            Screen.Chat(username = it)
                                                .let(block = navController::navigate)
                                        }
                                    )
                                }
                                composable<Screen.Chat> { backStackEntry ->
                                    val chat: Screen.Chat = backStackEntry.toRoute()
                                    ChatScreen(username = chat.username)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
