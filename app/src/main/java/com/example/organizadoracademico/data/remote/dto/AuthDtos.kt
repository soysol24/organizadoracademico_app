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
    val password: String,
    val fotoPerfil: String? = null
)

data class AuthResponseDto(
    @SerializedName("user") val user: AuthUserDto,
    @SerializedName("token") val token: String
)

data class ErrorResponseDto(
    @SerializedName("error") val error: String
)

