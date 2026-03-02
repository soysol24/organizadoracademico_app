package com.example.organizadoracademico.domain.model

data class Usuario(
    val id: Int = 0,
    val nombre: String = "",
    val email: String = "",
    val password: String = "",
    val fotoPerfil: String? = null
)