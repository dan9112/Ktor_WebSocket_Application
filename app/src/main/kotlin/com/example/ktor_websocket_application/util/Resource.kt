package com.example.ktor_websocket_application.util

sealed interface Resource<T> {
    val data: T?
        get() = null

    class Success<T>(override val data: T?) : Resource<T>
    class Error<T>(val message: String, override val data: T? = null) : Resource<T>
}
