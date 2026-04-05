package com.example.organizadoracademico.data.remote.dto

data class PushTokenRequestDto(
    val token: String,
    val platform: String = "android"
)

