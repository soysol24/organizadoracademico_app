package com.example.organizadoracademico.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginRequestDto(
    val email: String,
    val password: String
)

data class RegisterRequestDto(
    val nombre: String,
    val email: String,
    val password: String,
    val fotoPerfil: String? = null
)

data class AuthUserDto(
    val id: Int,
    val nombre: String,
    val email: String,
    val password: String? = null,
    val fotoPerfil: String? = null
)

data class AuthResponseDto(
    @SerializedName(value = "user", alternate = ["usuario"]) val user: AuthUserDto,
    @SerializedName(value = "token", alternate = ["accessToken"]) val token: String? = null
)

data class ErrorResponseDto(
    @SerializedName("error") val error: String
)

